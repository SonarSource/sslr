/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.JXPathNotFoundException;

import com.sonar.sslr.api.AstNode;

public final class AstNodeXpathQuery<TYPE> {

  private final CompiledExpression expression;

  static {
    JXPathIntrospector.registerDynamicClass(AstNode.class, AstNodePropertyHandler.class);
    JXPathIntrospector.registerDynamicClass(AstNodeWrapper.class, AstNodeWrapperPropertyHandler.class);
  }

  private AstNodeXpathQuery(String xpath) {
    expression = JXPathContext.compile(xpath);
  }

  public TYPE getValue(AstNode astNode) {
    try {
      JXPathContext context = JXPathContext.newContext(new AstNodeWrapper(astNode));
      return (TYPE) expression.getValue(context);
    } catch (JXPathNotFoundException e) {
      return null;
    }
  }

  public List<TYPE> getValues(AstNode astNode) {
    List<TYPE> result = new ArrayList<TYPE>();

    JXPathContext context = JXPathContext.newContext(new AstNodeWrapper(astNode));
    Iterator<TYPE> it = expression.iterate(context);

    while (it.hasNext()) {
      result.add(it.next());
    }

    return result;
  }

  public static <E> AstNodeXpathQuery<E> create(String xpath) {
    return new AstNodeXpathQuery<E>(xpath);
  }

}
