/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.Dsl;
import com.sonar.sslr.dsl.DslTokenType;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class Compiler {

  private Map<AstNode, Object> adapterByAstNode = new HashMap<AstNode, Object>();
  private Dsl dsl;
  private String source;
  private AdapterRepository adapters = new AdapterRepository();

  public Compiler(Dsl dsl, String source) {
    this.dsl = dsl;
    this.source = source;
  }

  public Bytecode transform(AstNode astNode) {
    List<Object> stmts = new ArrayList<Object>();
    feedStmtList(astNode, stmts);
    return new Bytecode(stmts);
  }

  private void feedStmtList(AstNode astNode, List<Object> stmts) {
    instanciateAdapter(astNode);
    feedStmtListOnChildren(astNode, stmts);
    addExecutableAdapter(astNode, stmts);
    feedParentAttributes(astNode);
  }

  private void instanciateAdapter(AstNode astNode) {
    Object adapter = getAdapter(astNode);
    if (adapter != null) {
      adapterByAstNode.put(astNode, adapter);
    }
  }

  private void feedParentAttributes(AstNode astNode) {
    if (astNode.is(DslTokenType.LITERAL) && adapterByAstNode.containsKey(astNode.getParent())) {
      Object parentAdapter = adapterByAstNode.get(astNode.getParent());
      ReflexionUtil.call(parentAdapter, "setLiteral", astNode.getTokenValue());
    }

  }

  private void addExecutableAdapter(AstNode astNode, List<Object> stmts) {
    if (adapterByAstNode.containsKey(astNode) && ReflexionUtil.hasMethod(adapterByAstNode.get(astNode).getClass(), "execute")) {
      stmts.add(adapterByAstNode.get(astNode));
    }
  }

  private void feedStmtListOnChildren(AstNode astNode, List<Object> stmts) {
    if (astNode.hasChildren()) {
      for (AstNode child : astNode.getChildren()) {
        feedStmtList(child, stmts);
      }
    }
  }

  private Object getAdapter(AstNode astNode) {
    if (astNode.getType() instanceof RuleImpl) {
      RuleImpl rule = (RuleImpl) astNode.getType();
      if (rule.getAdapter() != null) {
        return adapters.newInstance(rule.getAdapter());
      }
    }
    return null;
  }

  public void inject(Object component) {
    adapters.inject(component);

  }

  public Bytecode compile() {
    Parser<Grammar> parser = new DefaultDslParser(dsl);
    AstNode ast = parser.parse(source);
    return transform(ast);
  }

}
