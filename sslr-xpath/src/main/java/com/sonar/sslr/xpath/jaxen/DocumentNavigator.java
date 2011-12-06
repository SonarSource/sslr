/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.xpath.jaxen;

import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;

import com.sonar.sslr.api.AstNode;

public class DocumentNavigator
    extends DefaultNavigator
    implements NamedAccessNavigator {

  /**
   * Singleton implementation.
   */
  private static final DocumentNavigator instance = new DocumentNavigator();

  /**
   * Retrieve the singleton instance of this <code>DocumentNavigator</code>.
   */
  public static Navigator getInstance() {
    return instance;
  }

  public boolean isElement(Object obj) {
    return (obj instanceof AstNode);
  }

  public boolean isComment(Object obj) {
    return false;
  }

  public boolean isText(Object obj) {
    return (obj instanceof String);
  }

  public boolean isAttribute(Object obj) {
    return false;
  }

  public boolean isProcessingInstruction(Object obj) {
    return false;
  }

  public boolean isDocument(Object obj) {
    return false;
  }

  public boolean isNamespace(Object obj) {
    return false;
  }

  public String getElementName(Object obj) {
    return ((AstNode) obj).getName();
  }

  public String getElementNamespaceUri(Object obj) {
    return "";
  }

  public String getElementQName(Object obj) {
    return "";
  }

  public String getAttributeName(Object obj) {
    return "";
  }

  public String getAttributeNamespaceUri(Object obj) {
    return "";
  }

  public String getAttributeQName(Object obj) {
    return "";
  }

  public Iterator getChildAxisIterator(Object contextNode) {
    return JaxenConstants.EMPTY_ITERATOR;
  }

  public Iterator getChildAxisIterator(Object contextNode,
                                         String localName,
                                         String namespacePrefix,
                                         String namespaceURI) {
    return new AstNodeIterator((AstNode) contextNode, localName);
  }

  public Iterator getParentAxisIterator(Object contextNode) {
    if (contextNode instanceof AstNode) {
      return new SingleObjectIterator(((AstNode) contextNode).getParent());
    }

    return JaxenConstants.EMPTY_ITERATOR;
  }

  public Iterator getAttributeAxisIterator(Object contextNode) {
    return JaxenConstants.EMPTY_ITERATOR;
  }

  public Iterator getAttributeAxisIterator(Object contextNode,
                                             String localName,
                                             String namespacePrefix,
                                             String namespaceURI) {
    return JaxenConstants.EMPTY_ITERATOR;
  }

  public Iterator getNamespaceAxisIterator(Object contextNode) {
    return JaxenConstants.EMPTY_ITERATOR;
  }

  public Object getDocumentNode(Object contextNode) {
    return contextNode;
  }

  public Object getParentNode(Object contextNode) {
    if (contextNode instanceof AstNode) {
      return ((AstNode) contextNode).getParent();
    }

    return JaxenConstants.EMPTY_ITERATOR;
  }

  public String getTextStringValue(Object obj) {
    if (obj instanceof AstNode) {
      return ((AstNode) obj).getName();
    }
    return obj.toString();
  }

  public String getElementStringValue(Object obj) {
    if (obj instanceof AstNode) {
      return ((AstNode) obj).getName();
    }
    return obj.toString();
  }

  public String getAttributeStringValue(Object obj) {
    return obj.toString();
  }

  public String getCommentStringValue(Object obj) {
    return null;
  }

  public short getNodeType(Object node) {
    return 0;
  }

  public XPath parseXPath(String xpath)
        throws org.jaxen.saxpath.SAXPathException {
    return new AstNodeXPath(xpath);
  }

  public String getNamespaceStringValue(Object ns) {
    return null;
  }

  public String getNamespacePrefix(Object ns) {
    return null;
  }
}
