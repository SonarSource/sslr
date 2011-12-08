/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.xpath;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.lang.NotImplementedException;

import com.sonar.sslr.api.AstNode;

public class AstNodePropertyHandler implements DynamicPropertyHandler {

  public Object getProperty(Object node, String propertyName) {
    return getPropertyImpl(node, propertyName);
  }

  public static Object getPropertyImpl(Object node, String propertyName) {
    AstNode astNode = (AstNode) node;

    if ("tokenValue".equals(propertyName)) {
      return astNode.getTokenValue();
    } else if ("tokenLine".equals(propertyName)) {
      return getTokenLine(astNode);
    } else if ("tokenColumn".equals(propertyName)) {
      return getTokenColumn(astNode);
    }

    List<AstNode> matchingChildren = new ArrayList<AstNode>();
    for (AstNode child : astNode.getChildren()) {
      if (child.getName().equals(propertyName)) {
        matchingChildren.add(child);
      }
    }

    Object result = null;
    if (matchingChildren.size() == 1) {
      result = matchingChildren.get(0);

    } else if (matchingChildren.size() > 1) {
      result = matchingChildren;
    }

    return result;
  }

  private static Integer getTokenColumn(AstNode astNode) {
    return hasTokenLineAndTokenColumn(astNode) ? astNode.getToken().getColumn() : null;
  }

  private static Integer getTokenLine(AstNode astNode) {
    return hasTokenLineAndTokenColumn(astNode) ? astNode.getToken().getLine() : null;
  }

  public String[] getPropertyNames(Object node) {
    return getPropertyNamesImpl(node);
  }

  public static String[] getPropertyNamesImpl(Object node) {
    AstNode astNode = (AstNode) node;

    List<AstNode> children = astNode.getChildren();

    int numberOfProperties = children == null ? 0 : children.size();
    if (hasTokenValue(astNode)) {
      numberOfProperties += 1;
    }
    if (hasTokenLineAndTokenColumn(astNode)) {
      numberOfProperties += 2;
    }

    String[] propertyNames = new String[numberOfProperties];

    int i = 0;
    if (hasTokenValue(astNode)) {
      propertyNames[i++] = "tokenValue";
    }
    if (hasTokenLineAndTokenColumn(astNode)) {
      propertyNames[i++] = "tokenLine";
      propertyNames[i++] = "tokenColumn";
    }

    for (int j = 0; i < propertyNames.length && j < children.size(); j++) {
      propertyNames[i++] = children.get(j).getName();
    }

    return propertyNames;
  }

  public void setProperty(Object arg0, String arg1, Object arg2) {
    throw new NotImplementedException();
  }

  private static boolean hasTokenValue(AstNode node) {
    return node.getTokenValue() != null;
  }

  private static boolean hasTokenLineAndTokenColumn(AstNode node) {
    return node.hasToken();
  }

}
