/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
