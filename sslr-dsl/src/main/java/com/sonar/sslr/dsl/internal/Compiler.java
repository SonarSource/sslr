/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.DslException;
import com.sonar.sslr.dsl.adapter.ControlFlowAdapter;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.matcher.RuleDefinition;

public class Compiler {

  private Grammar dsl;
  private String source;

  private MutablePicoContainer pico = new DefaultPicoContainer();
  private Map<AstNode, Object> adapterByAstNode = new HashMap<AstNode, Object>();

  public Compiler(Grammar dsl, String source) {
    this.dsl = dsl;
    this.source = source;
  }

  private Bytecode transform(AstNode astNode) {
    Bytecode bytecode = new Bytecode();
    feedStmtList(astNode, bytecode);
    injectChildrenAdapters(astNode, astNode.getChildren());
    return bytecode;
  }

  private void feedStmtList(AstNode astNode, Bytecode bytecode) {
    Object adapter = getAdapter(astNode);
    bytecode.startControlFlowAdapter(adapter);
    feedStmtListOnChildren(astNode, bytecode);
    bytecode.endControlFlowAdapter(adapter);
    bytecode.addAdapter(adapter);
  }

  private void injectChildrenAdapters(AstNode astNode, List<AstNode> children) {
    if (children != null) {
      for (AstNode child : children) {
        injectChildrenAdapters(child, child.getChildren());
      }
    }
    Object adapterInstance = adapterByAstNode.get(astNode);
    String ruleName = astNode.getName();

    if (adapterInstance != null) {
      Object parentAdapterInstance = findNearestParentAdapter(astNode);
      if (parentAdapterInstance != null) {
        String methodName = "add" + Character.toUpperCase(ruleName.charAt(0)) + ruleName.substring(1);
        for (Method method : parentAdapterInstance.getClass().getMethods()) {
          if (method.getName().equals(methodName)) {
            try {
              method.invoke(parentAdapterInstance, adapterInstance);
              return;
            } catch (Exception e) {
              throw new DslException("Unable to call method '" + parentAdapterInstance.getClass().getName() + "." + methodName + "("
                  + adapterInstance.getClass().getName() + ")", e);
            }
          }
        }
        methodName = "add";
        for (Method method : parentAdapterInstance.getClass().getMethods()) {
          if (method.getName().equals(methodName)) {
            try {
              method.invoke(parentAdapterInstance, adapterInstance);
              return;
            } catch (Exception e) {
              if ( !(parentAdapterInstance instanceof ControlFlowAdapter)) {
                throw new DslException("Unable to call method '" + parentAdapterInstance.getClass().getName() + "." + methodName + "("
                    + adapterInstance.getClass().getName() + ")", e);
              }
            }
          }
        }
        if ( !(parentAdapterInstance instanceof ControlFlowAdapter)) {
          throw new DslException("The method '" + parentAdapterInstance.getClass().getName() + "." + methodName + "("
              + adapterInstance.getClass().getName() + ") is missing");
        }
      }
    }
  }

  private Object findNearestParentAdapter(AstNode astNode) {
    if (astNode.getParent() != null) {
      if (adapterByAstNode.containsKey(astNode.getParent())) {
        return adapterByAstNode.get(astNode.getParent());
      }
      return findNearestParentAdapter(astNode.getParent());
    }
    return null;
  }

  private void feedStmtListOnChildren(AstNode astNode, Bytecode bytecode) {
    if (astNode.hasChildren()) {
      for (AstNode child : astNode.getChildren()) {
        feedStmtList(child, bytecode);
      }
    }
  }

  private Object getAdapter(AstNode astNode) {
    if (astNode.getType() instanceof RuleDefinition) {
      RuleDefinition rule = (RuleDefinition) astNode.getType();
      if (rule.getAdapter() != null) {
        Object adapterInstance;
        if (rule.getAdapter() == String.class) {
          adapterInstance = new String(astNode.getTokenValue());
        } else if (rule.getAdapter() == Integer.class) {
          adapterInstance = new Integer(astNode.getTokenValue());
        } else if (rule.getAdapter() == Double.class) {
          adapterInstance = new Double(astNode.getTokenValue());
        } else if (rule.getAdapter() == Boolean.class) {
          adapterInstance = new Boolean(astNode.getTokenValue());
        } else {
          if (pico.getComponent(rule.getAdapter()) == null) {
            pico.addComponent(rule.getAdapter());
          }
          adapterInstance = pico.getComponent(rule.getAdapter());
        }
        adapterByAstNode.put(astNode, adapterInstance);
        return adapterInstance;
      }
    }
    return null;
  }

  public void inject(Object component) {
    pico.addComponent(component);
  }

  public Bytecode compile() {
    Parser<Grammar> parser = new DefaultDslParser(dsl);
    AstNode ast = parser.parse(source);
    return transform(ast);
  }

}
