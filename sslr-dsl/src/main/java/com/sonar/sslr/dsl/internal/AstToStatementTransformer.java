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

import org.picocontainer.MutablePicoContainer;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.DslTokenType;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class AstToStatementTransformer {

  private MutablePicoContainer pico;
  private Map<Class, Object> adapterByClass = new HashMap<Class, Object>();
  private Map<AstNode, Object> adapterByAstNode = new HashMap<AstNode, Object>();

  public AstToStatementTransformer(MutablePicoContainer pico) {
    this.pico = pico;
  }

  public List<Object> transform(AstNode astNode) {
    List<Object> stmts = new ArrayList<Object>();
    feedStmtList(astNode, stmts);
    return stmts;
  }

  private void feedStmtList(AstNode astNode, List<Object> stmts) {
    instanciateAdapter(astNode);
    feedStmtListOnChildren(astNode, stmts);
    addExecutableAdapter(astNode, stmts);
    feedParentAttributes(astNode);
  }

  private void instanciateAdapter(AstNode astNode) {
    Class adapterClass = getAdapter(astNode);
    if (adapterClass != null) {
      Object adapter = adapterByClass.get(adapterClass);
      if (adapter == null) {
        pico.addComponent(adapterClass);
        adapter = pico.getComponent(adapterClass);
        adapterByClass.put(adapterClass, adapter);
      }
      adapterByAstNode.put(astNode, adapter);
    }
  }

  private void feedParentAttributes(AstNode astNode) {
    if (astNode.is(DslTokenType.LITERAL) && adapterByAstNode.containsKey(astNode.getParent())) {
      Object parentAdapter = adapterByAstNode.get(astNode.getParent());
      Reflexion.call(parentAdapter, "setLiteral", astNode.getTokenValue());
    }

  }

  private void addExecutableAdapter(AstNode astNode, List<Object> stmts) {
    if (adapterByAstNode.containsKey(astNode) && Reflexion.hasMethod(adapterByAstNode.get(astNode).getClass(), "execute")) {
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

  private Class getAdapter(AstNode astNode) {
    if (astNode.getType() instanceof RuleImpl) {
      RuleImpl rule = (RuleImpl) astNode.getType();
      return rule.getAdapter();
    }
    return null;
  }

}
