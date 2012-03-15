/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.xpath;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;

import java.util.Collections;
import java.util.Iterator;

import static com.google.common.base.Preconditions.*;

@SuppressWarnings("serial")
public class AstNodeNavigator extends DefaultNavigator {

  private final static Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();

  private AstNode wrappedDocumentAstNode;

  public static AstNode getWrappedDocumentAstNode(AstNode documentAstNode) {
    AstNode wrappedDocumentAstNode = new AstNode(null, "[root]", null);
    wrappedDocumentAstNode.addChild(documentAstNode);
    return wrappedDocumentAstNode;
  }

  public void setDocumentAstNode(AstNode wrappedDocumentAstNode) {
    checkNotNull(wrappedDocumentAstNode, "wrappedDocumentAstNode cannot be null");
    this.wrappedDocumentAstNode = wrappedDocumentAstNode;
  }

  /* Type conversions */

  @Override
  public String getTextStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getCommentStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAttributeStringValue(Object attributeObject) {
    Attribute attribute = (Attribute) attributeObject;

    if ("tokenLine".equals(attribute.getName())) {
      return "" + attribute.getAstNode().getToken().getLine();
    } else if ("tokenColumn".equals(attribute.getName())) {
      return "" + attribute.getAstNode().getToken().getColumn();
    } else if ("tokenValue".equals(attribute.getName())) {
      return attribute.getAstNode().getToken().getOriginalValue();
    } else {
      throw new UnsupportedOperationException("Unsupported attribute name \"" + attribute.getName() + "\"");
    }
  }

  @Override
  public String getElementStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  /* Namespaces */

  @Override
  public String getNamespacePrefix(Object arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getNamespaceStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  /* Attributes */

  @Override
  public String getAttributeName(Object attributeObject) {
    Attribute attribute = (Attribute) attributeObject;

    return attribute.getName();
  }

  @Override
  public String getAttributeQName(Object attributeObject) {
    return getAttributeName(attributeObject);
  }

  @Override
  public String getAttributeNamespaceUri(Object arg0) {
    return "";
  }

  /* Elements */

  @Override
  public String getElementName(Object astNodeObject) {
    AstNode astNode = (AstNode) astNodeObject;
    return astNode.getName();
  }

  @Override
  public String getElementQName(Object astNodeObject) {
    return getElementName(astNodeObject);
  }

  @Override
  public String getElementNamespaceUri(Object astNodeObject) {
    return "";
  }

  /* Types */

  @Override
  public boolean isAttribute(Object object) {
    return object instanceof Attribute;
  }

  @Override
  public boolean isComment(Object object) {
    return false;
  }

  @Override
  public boolean isDocument(Object object) {
    return wrappedDocumentAstNode.equals(object);
  }

  @Override
  public boolean isElement(Object object) {
    return object instanceof AstNode;
  }

  @Override
  public boolean isNamespace(Object arg0) {
    return false;
  }

  @Override
  public boolean isProcessingInstruction(Object arg0) {
    return false;
  }

  @Override
  public boolean isText(Object arg0) {
    return false;
  }

  /* Navigation */

  @Override
  public Object getDocumentNode(Object contextNode) {
    return wrappedDocumentAstNode;
  }

  @Override
  public Iterator getChildAxisIterator(Object object) {
    if (isAttribute(object)) {
      return EMPTY_ITERATOR;
    } else if (isElement(object)) {
      AstNode astNode = (AstNode) object;
      return astNode.getChildren().iterator();
    } else {
      throw new UnsupportedOperationException("Unsupported parent object type \"" + object.getClass().getSimpleName() + "\"");
    }
  }

  @Override
  public Object getParentNode(Object object) {
    if (isAttribute(object)) {
      Attribute attribute = (Attribute) object;
      return attribute.getAstNode();
    } else {
      AstNode astNode = (AstNode) object;
      return astNode.getParent();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator getParentAxisIterator(Object object) {
    if (isAttribute(object)) {
      Attribute attribute = (Attribute) object;

      return new SingleObjectIterator(attribute.getAstNode());
    } else {
      AstNode astNode = (AstNode) object;
      AstNode parent = astNode.getParent();

      return parent == null ? EMPTY_ITERATOR : new SingleObjectIterator(parent);
    }
  }

  @Override
  public Iterator getAttributeAxisIterator(Object astNodeObject) {
    AstNode astNode = (AstNode) astNodeObject;

    if (!astNode.hasToken()) {
      return EMPTY_ITERATOR;
    } else {
      return Lists.newArrayList(
          new Attribute("tokenLine", astNode),
          new Attribute("tokenColumn", astNode),
          new Attribute("tokenValue", astNode)
          ).iterator();
    }
  }

  /* Unknown */

  @Override
  public XPath parseXPath(String arg0) {
    return null;
  }

  private class Attribute {

    private final String name;
    private final AstNode astNode;

    public Attribute(String name, AstNode astNode) {
      this.name = name;
      this.astNode = astNode;
    }

    public String getName() {
      return name;
    }

    public AstNode getAstNode() {
      return astNode;
    }

  }

}
