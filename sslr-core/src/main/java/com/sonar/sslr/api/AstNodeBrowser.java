/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.util.ArrayList;
import java.util.List;

public class AstNodeBrowser {

  private AstNode node;
  private List<AstNode> nodes;

  public AstNodeBrowser(AstNode rootNode) {
    this.node = rootNode;
  }

  public static AstNodeBrowser browse(AstNode rootNode) {
    return new AstNodeBrowser(rootNode);
  }

  public AstNodeBrowser findFirstDirectChild(AstNodeType... nodeTypes) {
    if (node != null) {
      node = node.findFirstDirectChild(nodeTypes);
    }
    return this;
  }

  public AstNodeBrowser findChildren(AstNodeType nodeType) {
    if (node != null) {
      nodes = node.findChildren(nodeType);
    }
    return this;
  }

  public AstNodeBrowser findFirstChild(AstNodeType... nodeTypes) {
    if (node != null) {
      node = node.findFirstChild(nodeTypes);
    }
    return this;
  }

  public AstNodeBrowser findFirstDirectChild(AstNodeType nodeType, String tokenValue) {
    if (node != null) {
      List<AstNode> words = node.findDirectChildren(nodeType);
      for (AstNode word : words) {
        if (word.getTokenValue().equals(tokenValue)) {
          node = word;
          return this;
        }
      }
    }
    node = null;
    return this;
  }

  public boolean hasResult() {
    return node != null || nodes != null;
  }

  public AstNode getResult() {
    return node;
  }

  public List<AstNode> getResults() {
    if (nodes != null) {
      return nodes;
    }
    return new ArrayList<AstNode>();
  }
}
