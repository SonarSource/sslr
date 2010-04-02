/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.api;

import java.util.ArrayList;
import java.util.List;

public class AstNode {

  protected final AstNodeType type;
  private String name;
  private Token token;
  private List<AstNode> children;
  private AstNode parent;
  private int fromIndex;
  private int toIndex;
  private boolean hasToBeSkipped = false;

  public AstNode(Token token) {
    this(token.getType(), token.getType().getName(), token);
  }

  public AstNode(AstNodeType type, String name, Token token) {
    this.type = type;
    this.token = token;
    this.name = name;
    hasToBeSkipped = type.hasToBeSkippedFromAst();
  }

  public AstNode(AstNodeType type, String name, Token token, boolean hasToBeSkipped) {
    this(type, name, token);
    this.hasToBeSkipped = hasToBeSkipped;
  }

  public AstNode getParent() {
    return parent;
  }

  public void addChild(AstNode child) {
    if (child != null) {
      if (children == null) {
        children = new ArrayList<AstNode>();
      }
      if (child.hasToBeSkipped()) {
        if (child.hasChildren()) {
          children.addAll(child.children);
          for (AstNode subChild : child.children) {
            subChild.parent = this;
          }
        }
      } else {
        children.add(child);
        child.parent = this;
      }
    }
  }

  public boolean hasChildren() {
    return children != null && !children.isEmpty();
  }

  public List<AstNode> getChildren() {
    return children;
  }

  public AstNode getNextSibling() {
    if (parent == null) {
      return null;
    }
    for (int i = 0; i < parent.children.size(); i++) {
      AstNode child = parent.children.get(i);
      if (child == this && parent.children.size() > i + 1) {
        return parent.children.get(i + 1);
      }
    }
    return parent.getNextSibling();
  }

  public String getTokenValue() {
    if (token == null) {
      return null;
    }
    return token.getValue();
  }

  public Token getToken() {
    return token;
  }

  public int getTokenLine() {
    return token.getLine();
  }

  public boolean hasToken() {
    return token != null;
  }

  public String getName() {
    return name;
  }

  public int getFromIndex() {
    return fromIndex;
  }

  public void setFromIndex(int fromIndex) {
    this.fromIndex = fromIndex;
  }

  public int getToIndex() {
    return toIndex;
  }

  public boolean hasToBeSkipped() {
    return hasToBeSkipped;
  }

  public void setToIndex(int toIndex) {
    this.toIndex = toIndex;
  }

  public boolean is(AstNodeType type) {
    return this.type == type;
  }

  public boolean isNot(AstNodeType type) {
    return this.type != type;
  }

  public AstNode findFirstDirectChild(AstNodeType... nodeTypes) {
    for (AstNode child : children) {
      for (AstNodeType nodeType : nodeTypes) {
        if (child.type == nodeType) {
          return child;
        }
      }
    }
    return null;
  }

  public AstNode findFirst(AstNodeType... nodeTypes) {
    if (children != null) {
      for (AstNode child : children) {
        for (AstNodeType nodeType : nodeTypes) {
          if (child.type == nodeType) {
            return child;
          }
          AstNode node = child.findFirst(nodeTypes);
          if (node != null) {
            return node;
          }
        }
      }
    }
    return null;
  }

  public AstNode getFirstChild() {
    if (children != null && children.size() > 0) {
      return children.get(0);
    }
    return null;
  }

  public List<AstNode> findDirectChildren(AstNodeType nodeType) {
    List<AstNode> nodes = new ArrayList<AstNode>();
    for (AstNode child : children) {
      if (child.type == nodeType) {
        nodes.add(child);
      }
    }
    return nodes;
  }

  public AstNode getLastChild() {
    if (children != null && children.size() > 0) {
      return children.get(children.size() - 1);
    }
    return null;
  }

  public boolean hasDirectChildren(AstNodeType nodeType) {
    return findFirstDirectChild(nodeType) != null;
  }

  public boolean hasSomewhere(AstNodeType nodeType) {
    return findFirst(nodeType) != null;
  }

  public boolean hasAmongParents(AstNodeType nodeType) {
    if (getFirtParent(nodeType) != null) {
      return true;
    }
    return false;
  }

  public AstNode getFirtParent(AstNodeType nodeType) {
    if (parent == null) {
      return null;
    } else if (parent.type == nodeType) {
      return parent;
    }
    return parent.getFirtParent(nodeType);
  }

  public boolean isCopyBookOrGeneratedNode() {
    return getToken().isCopyBook() || getToken().isGeneratedCode();
  }

  public AstNodeType getType() {
    return type;
  }
}
