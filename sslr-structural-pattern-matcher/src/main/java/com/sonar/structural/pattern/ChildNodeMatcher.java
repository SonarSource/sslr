/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public abstract class ChildNodeMatcher extends CompositeMatcher {

  protected String rule;
  protected String tokenValue;

  public void setRule(String name) {
    rule = name;
  }

  public void setTokenValue(Literal tokenValue) {
    this.tokenValue = tokenValue.toString();
  }

  @Override
  public final AstNode match(AstNode node) {
    if (node.getTokenValue().equals(tokenValue)) {
      return getLeafNode(node);
    }
    node = matchChildren(node);
    if (node != null) {
      if (matcher != null) {
        return matcher.match(node);
      } else {
        return node;
      }
    }
    return null;
  }
  
  private AstNode getLeafNode(AstNode nextNode) {
    if (nextNode.hasChildren()) {
      return getLeafNode(nextNode.getFirstChild());
    }
    return nextNode;
  }

  protected abstract AstNode matchChildren(AstNode node);
}
