/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public class AfterMatcher extends CompositeMatcher {

  private String tokenValue;
  private String rule;

  public void setTokenValue(Literal tokenValue) {
    this.tokenValue = tokenValue.toString();
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  @Override
  public AstNode match(AstNode node) {
    if (tokenValue != null) {
      AstNode nextNode = node.nextAstNode();
      if (nextNode != null && nextNode.getTokenValue().equals(tokenValue)) {
        return matchNext(getLeafNode(nextNode));
      }
    }
    if (rule != null) {
      AstNode nextNode = node.nextAstNode();
      while (nextNode != null) {
        if (nextNode.getName().equals(rule)) {
          return matchNext(nextNode);
        }
        nextNode = nextNode.getFirstChild();
      }
    }
    return null;
  }

  public AstNode matchNext(AstNode node) {
    if (matcher != null) {
      return matcher.match(node);
    } else return node;
  }

  private AstNode getLeafNode(AstNode nextNode) {
    if (nextNode.hasChildren()) {
      return getLeafNode(nextNode.getFirstChild());
    }
    return nextNode;
  }
}
