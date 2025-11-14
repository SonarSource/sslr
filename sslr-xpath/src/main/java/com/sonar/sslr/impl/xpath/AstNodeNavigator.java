/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.xpath;

import com.sonar.sslr.api.AstNode;
import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

@SuppressWarnings("serial")
public class AstNodeNavigator extends DefaultNavigator {

  private transient AstNode documentNode = null;

  public void reset() {
    documentNode = null;
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
      return Integer.toString(attribute.getAstNode().getToken().getLine());
    } else if ("tokenColumn".equals(attribute.getName())) {
      return Integer.toString(attribute.getAstNode().getToken().getColumn());
    } else if ("tokenValue".equals(attribute.getName())) {
      return attribute.getAstNode().getToken().getValue();
    } else {
      throw new UnsupportedOperationException("Unsupported attribute name \"" + attribute.getName() + "\"");
    }
  }

  @Override
  public String getElementStringValue(Object arg0) {
    throw new UnsupportedOperationException("Implicit nodes to string conversion is not supported. Use the tokenValue attribute instead.");
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
  public boolean isDocument(Object contextObject) {
    computeDocumentNode(contextObject);
    return documentNode == null ? false : documentNode.equals(contextObject);
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
    Objects.requireNonNull(documentNode, "Unable to compute the document node from the context node \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    return documentNode;
  }

  @Override
  public Iterator getChildAxisIterator(Object contextNode) {
    if (isElement(contextNode)) {
      AstNode astNode = (AstNode) contextNode;
      return astNode.getChildren().iterator();
    } else if (isAttribute(contextNode)) {
      return Collections.emptyIterator();
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
      return parent == null ? Collections.emptyIterator() : new SingleObjectIterator(parent);
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
        return Collections.emptyIterator();
      } else {
        return Arrays.asList(
          new Attribute("tokenLine", astNode),
          new Attribute("tokenColumn", astNode),
          new Attribute("tokenValue", astNode)
        ).iterator();
      }
    } else if (isAttribute(contextNode)) {
      return Collections.emptyIterator();
    } else {
      throw new UnsupportedOperationException("Unsupported context object type for attribute axis \"" + contextNode.getClass().getSimpleName() + "\": " + contextNode);
    }
  }

  /* Unknown */

  @Override
  public XPath parseXPath(String arg0) {
    return null;
  }

  // @VisibleForTesting
  public static class Attribute {

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
