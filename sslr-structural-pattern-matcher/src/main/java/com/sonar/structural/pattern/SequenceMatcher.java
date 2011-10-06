/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public class SequenceMatcher extends CompositeMatcher {

  private BeforeMatcher beforeMatcher;
  private AfterMatcher afterMatcher;

  public void setBeforeMatcher(BeforeMatcher beforeMatcher) {
    this.beforeMatcher = beforeMatcher;
  }

  public void setAfterMatcher(AfterMatcher afterMatcher) {
    this.afterMatcher = afterMatcher;
  }

  @Override
  public AstNode match(AstNode node) {
    node = matcher.match(node);
    if (node == null) {
      return null;
    }
    if (beforeMatcher != null && !beforeMatcher.isMatching(node)) {
      return null;
    }
    if (afterMatcher != null && !afterMatcher.isMatching(node)) {
      return null;
    }
    return node;
  }
}
