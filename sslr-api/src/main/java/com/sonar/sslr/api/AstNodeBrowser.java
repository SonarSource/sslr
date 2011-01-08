/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.util.List;

public class AstNodeBrowser {

  private AstNode node;

  public AstNodeBrowser(AstNode rootNode) {
    this.node = rootNode;
  }
  
  public static AstNodeBrowser browse(AstNode rootNode){
    return new AstNodeBrowser(rootNode);
  }

  public AstNodeBrowser getFirstDirectChild(AstNodeType... nodeTypes) {
    if (node != null) {
      node = node.findFirstDirectChild(nodeTypes);
    }
    return this;
  }

  public AstNodeBrowser getFirstDirectChild(AstNodeType nodeType, String tokenValue) {
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
    return node != null;
  }

  public AstNode getResult() {
    return node;
  }
}
