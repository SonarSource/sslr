package com.sonar.sslr.xpath;

import java.util.List;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.lang.NotImplementedException;

import com.sonar.sslr.api.AstNode;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

public class AstNodePropertyHandler implements DynamicPropertyHandler {

  public Object getProperty(Object node, String propertyName) {
    for (AstNode child : ((AstNode) node).getChildren()) {
      if (child.getName().equals(propertyName)) {
        return child;
      }
    }
    return null;
  }

  public String[] getPropertyNames(Object node) {
    List<AstNode> children = ((AstNode) node).getChildren();
    String[] propertyNames = new String[children.size()];
    for (int i = 0; i < children.size(); i++) {
      propertyNames[i] = children.get(i).getName();
    }
    return propertyNames;
  }

  public void setProperty(Object arg0, String arg1, Object arg2) {
    throw new NotImplementedException();
  }

}
