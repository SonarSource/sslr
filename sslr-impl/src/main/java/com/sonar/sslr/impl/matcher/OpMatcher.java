/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class OpMatcher extends Matcher {

  private Matcher matcher;

  public OpMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    boolean leftRecursionState = parsingState.hasPendingLeftRecursion();
    if (matcher.isMatching(parsingState)) {
      return matcher.match(parsingState);
    }
    parsingState.setLeftRecursionState(leftRecursionState);
    return null;
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  public String toString() {
    return "(" + matcher + ")?";
  }

}
