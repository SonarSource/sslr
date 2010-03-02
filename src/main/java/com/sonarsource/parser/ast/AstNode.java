/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.ast;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.lexer.Token;
import com.sonarsource.lexer.TokenType;
import com.sonarsource.parser.matcher.Matcher;
import com.sonarsource.parser.matcher.Rule;

public class AstNode {

  public final AstNodeType type;
  private String name;
  private Token token;
  private List<AstNode> children;
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
    if (type instanceof Matcher && !(type instanceof Rule)) {
      hasToBeSkipped = true;
    }
  }

  public AstNode(AstNodeType type, String name, Token token, boolean hasToBeSkipped) {
    this(type, name, token);
    this.hasToBeSkipped = hasToBeSkipped;
  }

  public void addChild(AstNode child) {
    if (child != null) {
      if (children == null) {
        children = new ArrayList<AstNode>();
      }
      if (child.hasToBeSkipped()) {
        if (child.hasChildren()) {
          children.addAll(child.children);
        }
      } else {
        children.add(child);
      }
    }
  }

  public boolean hasChildren() {
    return children != null && !children.isEmpty();
  }

  public List<AstNode> getChildren() {
    return children;
  }

  public String getTokenValue() {
    if (token == null || type instanceof Rule) {
      return null;
    }
    return token.getValue();
  }

  public TokenType getTokenType() {
    if (token == null || type instanceof Rule) {
      return null;
    }
    return token.getType();
  }

  public Token getToken() {
    return token;
  }

  public int getTokenLine() {
    return token.getLine();
  }

  public int getTokenColumn() {
    return token.getColumn();
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

  public AstNode findFirstNode(AstNodeType... nodeTypes) {
    for (AstNode child : children) {
      for (AstNodeType nodeType : nodeTypes) {
        if (child.type == nodeType) {
          return child;
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

  public List<AstNode> findNodes(AstNodeType nodeType) {
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

  public boolean hasNode(AstNodeType nodeType) {
    return findFirstNode(nodeType) != null;
  }

}
