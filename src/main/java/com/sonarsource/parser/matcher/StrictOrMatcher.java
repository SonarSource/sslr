/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;

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
  public void setParentRule(Rule parentRule) {
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
