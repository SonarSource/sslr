package com.sonar.sslr.xpath;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathIntrospector;

import com.sonar.sslr.api.AstNode;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

public class AstNodeXpathExpression {

  private final CompiledExpression expression;

  public AstNodeXpathExpression(String xpath) {
    JXPathIntrospector.registerDynamicClass(AstNode.class, AstNodePropertyHandler.class);
    expression = JXPathContext.compile(xpath);
  }

  public AstNode search(AstNode astNode) {
    JXPathContext context = JXPathContext.newContext(astNode);
    return (AstNode) expression.getValue(context);
  }

}
