/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.xpath;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.lang.NotImplementedException;

import com.sonar.sslr.api.AstNode;

public class AstNodeDocumentPropertyHandler implements DynamicPropertyHandler {

  // Because of the predicates, we also need to support all the properties of the wrapped object

  public Object getProperty(Object node, String propertyName) {
    AstNodeDocument astNodeRootWrapper = (AstNodeDocument) node;
    AstNode wrappedAstNode = astNodeRootWrapper.getWrappedAstNode();

    return wrappedAstNode.getName().equals(propertyName) ? wrappedAstNode : AstNodePropertyHandler.getPropertyImpl(wrappedAstNode,
        propertyName);
  }

  public String[] getPropertyNames(Object node) {
    AstNodeDocument astNodeRootWrapper = (AstNodeDocument) node;
    AstNode wrappedAstNode = astNodeRootWrapper.getWrappedAstNode();

    String[] wrappedPropertyNames = AstNodePropertyHandler.getPropertyNamesImpl(wrappedAstNode);
    String[] propertyNames = new String[wrappedPropertyNames.length + 1];
    int i = 0;
    propertyNames[i++] = wrappedAstNode.getName();
    for (int j = 0; i < propertyNames.length && j < wrappedPropertyNames.length; j++) {
      propertyNames[i++] = wrappedPropertyNames[j];
    }

    return propertyNames;
  }

  public void setProperty(Object arg0, String arg1, Object arg2) {
    throw new NotImplementedException();
  }

}
