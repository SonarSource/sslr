/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

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
    return matcher.match(node);
  }
}
