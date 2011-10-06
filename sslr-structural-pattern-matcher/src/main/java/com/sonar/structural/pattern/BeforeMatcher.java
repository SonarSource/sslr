/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public class BeforeMatcher extends StructuralUnitMatcher {

  private String tokenValue;
  private String rule;
  private BeforeMatcher previousBeforeMatcher;

  public void setTokenValue(Literal tokenValue) {
    this.tokenValue = tokenValue.toString();
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public void setBeforeMatcher(BeforeMatcher previousBeforeMatcher) {
    this.previousBeforeMatcher = previousBeforeMatcher;
  }

  @Override
  public AstNode match(AstNode node) {
    if (tokenValue != null) {
      AstNode previousNode = node.previousAstNode();
      while (previousNode != null) {
        if (previousNode.getTokenValue().equals(tokenValue)) {
          return matchPrevious(previousNode);
        }
        previousNode = previousNode.getLastChild();
      }
    }
    if (rule != null) {
      AstNode previousNode = node.previousAstNode();
      while (previousNode != null) {
        if (previousNode.getName().equals(rule)) {
          return matchPrevious(previousNode);
        }
        previousNode = previousNode.getLastChild();
      }
    }
    return null;
  }

  public AstNode matchPrevious(AstNode node) {
    if (previousBeforeMatcher != null) {
      return previousBeforeMatcher.match(node);
    } else return node;
  }

  private AstNode getLeafNode(AstNode previousNode) {
    if (previousNode.hasChildren()) {
      return getLeafNode(previousNode.getLastChild());
    }
    return previousNode;
  }
}
