/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.Dsl;
import com.sonar.sslr.dsl.DslTokenType;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class Compiler {

  private Dsl dsl;
  private String source;
  private AdapterRepository adapters = new AdapterRepository();

  public Compiler(Dsl dsl, String source) {
    this.dsl = dsl;
    this.source = source;
  }

  public Bytecode transform(AstNode astNode) {
    Bytecode bytecode = new Bytecode();
    feedStmtList(astNode, bytecode);
    return bytecode;
  }

  private void feedStmtList(AstNode astNode, Bytecode bytecode) {
    Object adapter = getAdapter(astNode);
    feedStmtListOnChildren(astNode, bytecode);
    bytecode.addInstruction(adapter);
    feedParentAttributes(astNode);
  }

  private void feedParentAttributes(AstNode astNode) {
    if ( !astNode.hasChildren() && astNode.getType() instanceof DslTokenType) {
      DslTokenType dslTokenType = (DslTokenType) astNode.getType();
      Object adapter = dslTokenType.formatDslValue(astNode.getTokenValue());
      if (adapter != null) {
        adapters.plug(adapter, astNode);
      } else if (astNode.getParent().getNumberOfChildren() == 1) {
        adapters.plug(astNode.getTokenValue(), astNode);
      }

    }
    adapters.injectAdapter(astNode.getParent(), astNode);
  }

  private void feedStmtListOnChildren(AstNode astNode, Bytecode bytecode) {
    if (astNode.hasChildren()) {
      for (AstNode child : astNode.getChildren()) {
        feedStmtList(child, bytecode);
      }
    }
  }

  private Object getAdapter(AstNode astNode) {
    if (astNode.getType() instanceof RuleImpl) {
      RuleImpl rule = (RuleImpl) astNode.getType();
      if (rule.getAdapter() != null) {
        return adapters.plug(rule.getAdapter(), astNode);
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
