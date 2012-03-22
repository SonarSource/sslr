/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.xpath.AstNodeNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import java.util.List;

public final class AstNodeXPathQuery<TYPE> {

  private final AstNodeNavigator astNodeNavigator = new AstNodeNavigator();
  private final BaseXPath expression;

  private AstNodeXPathQuery(String xpath) {
    try {
      expression = new BaseXPath(xpath, astNodeNavigator);
    } catch (JaxenException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Evaluate the XPath query on the given AstNode and returns the first result, or null if there was no result.
   *
   * <pre>
   * In the following case, AstNodeXpathQuery.create('/a/b').selectSingleNode(node) would return the AstNode of B2.
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
   * @return The first result or null if there was no result.
   */
  public TYPE selectSingleNode(AstNode astNode) {
    try {
      return (TYPE) expression.selectSingleNode(astNode);
    } catch (JaxenException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Evaluate the XPath query on the given AstNode and returns all matching elements.
   *
   * <pre>
   * In the following case, AstNodeXpathQuery.create('/a/b').selectNodes(node) would return the AstNode of B2 and B3, in that order.
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
  public List<TYPE> selectNodes(AstNode astNode) {
    try {
      return expression.selectNodes(astNode);
    } catch (JaxenException e) {
      throw new RuntimeException(e);
    }
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
