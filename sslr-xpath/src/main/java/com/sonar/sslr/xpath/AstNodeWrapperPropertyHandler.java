/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.lang.NotImplementedException;

import com.sonar.sslr.api.AstNode;

public class AstNodeWrapperPropertyHandler implements DynamicPropertyHandler {

  // Because of the predicates, we also need to support all the properties of the wrapped object

  public Object getProperty(Object node, String propertyName) {
    AstNodeWrapper astNodeRootWrapper = (AstNodeWrapper) node;
    AstNode wrappedAstNode = astNodeRootWrapper.getWrappedAstNode();

    return wrappedAstNode.getName().equals(propertyName) ? wrappedAstNode : AstNodePropertyHandler.getPropertyImpl(wrappedAstNode,
        propertyName);
  }

  public String[] getPropertyNames(Object node) {
    AstNodeWrapper astNodeRootWrapper = (AstNodeWrapper) node;
    AstNode wrappedAstNode = astNodeRootWrapper.getWrappedAstNode();

    String[] wrappedPropertyNames = AstNodePropertyHandler.getPropertyNamesImpl(wrappedAstNode);
    String[] propertyNames = new String[wrappedPropertyNames.length + 1];
    propertyNames[0] = wrappedAstNode.getName();
    for (int i = 0; i < wrappedPropertyNames.length; i++) {
      propertyNames[i + 1] = wrappedPropertyNames[i];
    }

    return propertyNames;
  }

  public void setProperty(Object arg0, String arg1, Object arg2) {
    throw new NotImplementedException();
  }

}
