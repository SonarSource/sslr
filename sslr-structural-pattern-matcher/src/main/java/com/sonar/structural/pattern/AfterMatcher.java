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
  private RuleMatcher ruleMatcher;

  public void setTokenValue(Literal tokenValue) {
    this.tokenValue = tokenValue.toString();
  }

  public void setRuleMatcher(RuleMatcher ruleMatcher) {
    this.ruleMatcher = ruleMatcher;
  }

  @Override
  public AstNode match(AstNode node) {
    AstNode nextNode = node.nextAstNode();
    if (tokenValue != null) {
      if (nextNode != null && nextNode.getTokenValue().equals(tokenValue)) {
        return matchNext(getLeafNode(nextNode));
      }
    }
    if (ruleMatcher != null) {
      ruleMatcher.match(nextNode);
      return matchNext(ruleMatcher.match(nextNode));
    }
    return null;
  }

  public AstNode matchNext(AstNode node) {
    if (matcher != null && node != null) {
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
