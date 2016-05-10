/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.xpath.api;

import com.google.common.base.Throwables;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.xpath.AstNodeNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import java.util.List;

public final class AstNodeXPathQuery<T> {

  private final AstNodeNavigator astNodeNavigator = new AstNodeNavigator();
  private final BaseXPath expression;

  private AstNodeXPathQuery(String xpath) {
    try {
      expression = new BaseXPath(xpath, astNodeNavigator);
    } catch (JaxenException e) {
      throw Throwables.propagate(e);
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
  public T selectSingleNode(AstNode astNode) {
    try {
      astNodeNavigator.reset();
      return (T) expression.selectSingleNode(astNode);
    } catch (JaxenException e) {
      throw Throwables.propagate(e);
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
  public List<T> selectNodes(AstNode astNode) {
    try {
      astNodeNavigator.reset();
      return expression.selectNodes(astNode);
    } catch (JaxenException e) {
      throw Throwables.propagate(e);
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
