/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class StrictOrMatcher extends OrMatcher {

  public StrictOrMatcher(Matcher... matchers) {
    super(matchers);
  }

  public AstNode match(ParsingState parsingState) {
    Matcher matchingMatcher = null;
    int matchingMatchers = 0;
    for (Matcher matcher : super.children) {
      if (matcher.isMatching(parsingState)) {
        matchingMatchers++;
        matchingMatcher = matcher;
      }
    }
    if (matchingMatchers == 1 && matchingMatcher != null) {
      return matchingMatcher.match(parsingState);
    } else if (matchingMatchers > 1) {
      throw new IllegalStateException("There are two possible ways on rule " + getRule());
    }
    throw RecognitionExceptionImpl.create();
  }

  public String toString() {
    StringBuilder expr = new StringBuilder("(");
    for (int i = 0; i < super.children.length; i++) {
      expr.append(super.children[i]);
      if (i < super.children.length - 1) {
        expr.append(" | ");
      }
    }
    expr.append(")");
    return expr.toString();
  }
  
}
