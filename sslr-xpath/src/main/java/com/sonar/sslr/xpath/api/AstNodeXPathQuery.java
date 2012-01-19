/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.JXPathNotFoundException;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.xpath.AstNodeDocument;
import com.sonar.sslr.impl.xpath.AstNodeDocumentPropertyHandler;
import com.sonar.sslr.impl.xpath.AstNodePropertyHandler;

public final class AstNodeXPathQuery<TYPE> {

  private final CompiledExpression expression;

  static {
    JXPathIntrospector.registerDynamicClass(AstNode.class, AstNodePropertyHandler.class);
    JXPathIntrospector.registerDynamicClass(AstNodeDocument.class, AstNodeDocumentPropertyHandler.class);
  }

  private AstNodeXPathQuery(String xpath) {
    expression = JXPathContext.compile(xpath);
  }

  /**
   * Evaluate the XPath query on the given AstNode and returns the first result.
   * 
   * <pre>
   * In the following case, AstNodeXpathQuery.create('/a/b').getValue(node) would return the AstNode of B2.
   * 
   *   A1
   *    |__ C1
   *    |    |__ B1 
   *    |__ B2
   *    |__ B3
   * </pre>
   * 
   * @param astNode
   *          The AstNode on which to evaluate the query against to.
   * @return The resulting element, or null if no result available.
   */
  public TYPE getValue(AstNode astNode) {
    try {
      JXPathContext context = JXPathContext.newContext(new AstNodeDocument(astNode));
      return (TYPE) expression.getValue(context);
    } catch (JXPathNotFoundException e) {
      return null;
    }
  }

  /**
   * Evaluate the XPath query on the given AstNode and returns all matching elements.
   * 
   * <pre>
   * In the following case, AstNodeXpathQuery.create('/a/b').getValues(node) would return the AstNode of B2 and B3, in that order.
   * 
   *   A1
   *    |__ C1
   *    |    |__ B1 
   *    |__ B2
   *    |__ B3
   * </pre>
   * 
   * @param astNode
   *          The AstNode on which to evaluate the query against to.
   * @return The list of resulting elements, empty when no result available.
   */
  public List<TYPE> getValues(AstNode astNode) {
    List<TYPE> result = new ArrayList<TYPE>();

    JXPathContext context = JXPathContext.newContext(new AstNodeDocument(astNode));
    Iterator<TYPE> it = expression.iterate(context);

    while (it.hasNext()) {
      result.add(it.next());
    }

    return result;
  }

  /**
   * Creates a compiled XPath query, which can be evaluated multiple times on different AstNode.
   * 
   * @param xpath
   *          The query to compile
   * @return The compiled XPath query
   */
  public static <E> AstNodeXPathQuery<E> create(String xpath) {
    return new AstNodeXPathQuery<E>(xpath);
  }

}
