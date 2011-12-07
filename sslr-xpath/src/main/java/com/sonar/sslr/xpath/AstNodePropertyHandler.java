/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

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

    List<AstNode> result = new ArrayList<AstNode>();

    if ("tokenValue".equals(propertyName)) {
      return hasTokenValue(astNode) ? astNode.getTokenValue() : null;
    } else if ("tokenLine".equals(propertyName)) {
      return hasTokenLineAndTokenColumn(astNode) ? astNode.getToken().getLine() : null;
    } else if ("tokenColumn".equals(propertyName)) {
      return hasTokenLineAndTokenColumn(astNode) ? astNode.getToken().getColumn() : null;
    }

    for (AstNode child : astNode.getChildren()) {
      if (child.getName().equals(propertyName)) {
        result.add(child);
      }
    }

    if (result.isEmpty()) {
      return null;
    }

    return result.size() == 1 ? result.get(0) : result;
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
