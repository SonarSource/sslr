/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.structural.pattern;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.dsl.Literal;

public final class BeforeMatcher extends StructuralUnitMatcher {

  private String tokenValue;
  private OnRightRuleMatcher onRightRuleMatcher;
  private BeforeMatcher previousBeforeMatcher;

  public void setTokenValue(Literal tokenValue) {
    this.tokenValue = tokenValue.toString();
  }

  public void setOnRightRuleMatcher(OnRightRuleMatcher onRightRuleMatcher) {
    this.onRightRuleMatcher = onRightRuleMatcher;
  }

  public void setBeforeMatcher(BeforeMatcher previousBeforeMatcher) {
    this.previousBeforeMatcher = previousBeforeMatcher;
  }

  @Override
  public AstNode match(AstNode node) {
    if (tokenValue != null) {
      AstNode previousNode = node.previousAstNode();
      if (previousNode != null && previousNode.getLastToken().getValue().equals(tokenValue)) {
        return matchPrevious(previousNode);
      }
    }
    if (onRightRuleMatcher != null) {
      AstNode previousNode = node.previousAstNode();
      previousNode = onRightRuleMatcher.match(previousNode);
      if (previousNode != null) {
        return matchPrevious(previousNode);
      }
    }
    return null;
  }

  public AstNode matchPrevious(AstNode node) {
    if (previousBeforeMatcher != null) {
      return previousBeforeMatcher.match(node);
    }
    return node;
  }
}
