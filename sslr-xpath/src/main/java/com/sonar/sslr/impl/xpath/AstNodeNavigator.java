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

  private static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();

  private transient AstNode documentNode = null;

  /* Type conversions */

  public String getTextStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  public String getCommentStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

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

  public String getElementStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  /* Namespaces */

  public String getNamespacePrefix(Object arg0) {
    throw new UnsupportedOperationException();
  }

  public String getNamespaceStringValue(Object arg0) {
    throw new UnsupportedOperationException();
  }

  /* Attributes */

  public String getAttributeName(Object attributeObject) {
    Attribute attribute = (Attribute) attributeObject;

    return attribute.getName();
  }

  public String getAttributeQName(Object attributeObject) {
    return getAttributeName(attributeObject);
  }

  public String getAttributeNamespaceUri(Object arg0) {
    return "";
  }

  /* Elements */

  public String getElementName(Object astNodeObject) {
    AstNode astNode = (AstNode) astNodeObject;
    return astNode.getName();
  }

  public String getElementQName(Object astNodeObject) {
    return getElementName(astNodeObject);
  }

  public String getElementNamespaceUri(Object astNodeObject) {
    return "";
  }

  /* Types */

  public boolean isAttribute(Object object) {
    return object instanceof Attribute;
  }

  public boolean isComment(Object object) {
    return false;
  }

  public boolean isDocument(Object contextObject) {
    computeDocumentNode(contextObject);
    return documentNode == null ? false : documentNode.equals(contextObject);
  }

  public boolean isElement(Object object) {
    return object instanceof AstNode;
  }

  public boolean isNamespace(Object arg0) {
    return false;
  }

  public boolean isProcessingInstruction(Object arg0) {
    return false;
  }

  public boolean isText(Object arg0) {
    return false;
  }

  /* Navigation */

  private void computeDocumentNode(Object contextNode) {
    if (documentNode == null) {
      if (isElement(contextNode)) {
        AstNode root = (AstNode) contextNode;

        while (root.getParent() != null) {
          root = root.getParent();
        }

        documentNode = new AstNode(null, "[root]", null);
        documentNode.addChild(root);
      } else if (isAttribute(contextNode)) {
        Attribute attribute = (Attribute) contextNode;
        computeDocumentNode(attribute.getAstNode());
      }
    }
  }

  @Override
  public Object getDocumentNode(Object contextNode) {
    computeDocumentNode(contextNode);
    checkNotNull(documentNode, "Unable to compute the document node from the context node \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    return documentNode;
  }

  @Override
  public Iterator getChildAxisIterator(Object contextNode) {
    if (isElement(contextNode)) {
      AstNode astNode = (AstNode) contextNode;
      return astNode.getChildren().iterator();
    } else if (isAttribute(contextNode)) {
      return EMPTY_ITERATOR;
    } else {
      throw new UnsupportedOperationException("Unsupported context object type for child axis \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    }
  }

  @Override
  public Object getParentNode(Object contextNode) {
    if (isElement(contextNode)) {
      AstNode astNode = (AstNode) contextNode;
      return astNode.getParent();
    } else if (isAttribute(contextNode)) {
      Attribute attribute = (Attribute) contextNode;
      return attribute.getAstNode();
    } else {
      throw new UnsupportedOperationException("Unsupported context object type for parent node \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator getParentAxisIterator(Object contextNode) {
    if (isElement(contextNode)) {
      AstNode astNode = (AstNode) contextNode;
      AstNode parent = astNode.getParent();
      return parent == null ? EMPTY_ITERATOR : new SingleObjectIterator(parent);
    } else if (isAttribute(contextNode)) {
      Attribute attribute = (Attribute) contextNode;
      return new SingleObjectIterator(attribute.getAstNode());
    } else {
      throw new UnsupportedOperationException("Unsupported context object type for parent axis \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    }
  }

  @Override
  public Iterator getAttributeAxisIterator(Object contextNode) {
    if (isElement(contextNode)) {
      AstNode astNode = (AstNode) contextNode;

      if (!astNode.hasToken()) {
        return EMPTY_ITERATOR;
      } else {
        return Lists.newArrayList(
            new Attribute("tokenLine", astNode),
            new Attribute("tokenColumn", astNode),
            new Attribute("tokenValue", astNode)
            ).iterator();
      }
    } else if (isAttribute(contextNode)) {
      return EMPTY_ITERATOR;
    } else {
      throw new UnsupportedOperationException("Unsupported context object type for attribute axis \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    }
  }

  /* Unknown */

  public XPath parseXPath(String arg0) {
    return null;
  }

  private static class Attribute {

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
