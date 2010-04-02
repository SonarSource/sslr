/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.RecognitionException;
import com.sonarsource.sslr.api.AstNode;

public class StrictOrMatcher extends OrMatcher {

  private Matcher[] matchers;

  public StrictOrMatcher(Matcher... matchers) {
    this.matchers = matchers;
  }

  public AstNode match(ParsingState parsingState) {
    Matcher matchingMatcher = null;
    int matchingMatchers = 0;
    for (Matcher matcher : matchers) {
      if (matcher.isMatching(parsingState)) {
        matchingMatchers++;
        matchingMatcher = matcher;
      }
    }
    if (matchingMatchers == 1 && matchingMatcher != null) {
      return matchingMatcher.match(parsingState);
    } else if (matchingMatchers > 1) {
      throw new IllegalStateException("There are two possible ways.");
    }
    throw RecognitionException.create();
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    for (Matcher matcher : matchers) {
      matcher.setParentRule(parentRule);
    }
  }

  public String toString() {
    StringBuilder expr = new StringBuilder("(");
    for (int i = 0; i < matchers.length; i++) {
      expr.append(matchers[i]);
      if (i < matchers.length - 1) {
        expr.append(" | ");
      }
    }
    expr.append(")");
    return expr.toString();
  }
}
