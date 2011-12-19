/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public final class AfterMatcher extends StructuralUnitMatcher {

  private String tokenValue;
  private OnLeftRuleMatcher ruleMatcher;
  private AfterMatcher nextAfterMatcher;

  public void setTokenValue(Literal tokenValue) {
    this.tokenValue = tokenValue.toString();
  }

  public void setOnLeftRuleMatcher(OnLeftRuleMatcher ruleMatcher) {
    this.ruleMatcher = ruleMatcher;
  }

  public void setAfterMatcher(AfterMatcher nextAfterMatcher) {
    this.nextAfterMatcher = nextAfterMatcher;
  }

  @Override
  public AstNode match(AstNode node) {
    AstNode nextNode = node.nextAstNode();
    if (nextNode == null) {
      return null;
    }
    if (nextNode.getTokenValue().equals(tokenValue)) {
      return matchNext(getLeafNode(nextNode));
    }
    if (ruleMatcher != null) {
      return matchNext(ruleMatcher.match(nextNode));
    }
    return null;
  }

  public AstNode matchNext(AstNode node) {
    if (nextAfterMatcher != null && node != null) {
      return nextAfterMatcher.match(node);
    } else {
      return node;
    }
  }

  private AstNode getLeafNode(AstNode nextNode) {
    if (nextNode.hasChildren()) {
      return getLeafNode(nextNode.getFirstChild());
    }
    return nextNode;
  }
}
