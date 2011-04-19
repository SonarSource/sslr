/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class LongestOneMatcher extends Matcher {

  private Matcher[] matchers;

  public LongestOneMatcher(Matcher... matchers) {
    this.matchers = matchers;
  }

  public AstNode match(ParsingState parsingState) {
  	Matcher longestMatcher = null;
  	int longestMatchIndex = -1;
  	
    for (Matcher matcher : matchers) {
    	int matcherIndex = matcher.matchToIndex(parsingState);
      if (matcherIndex >= 0) {
        /* This matcher could parse the input [as well], but for longer than the current longest matcher? */
      	if (matcherIndex > longestMatchIndex) {
      		/* Yes! */
      		longestMatcher = matcher;
      		longestMatchIndex = matcherIndex;
      	}
      }
    }
    
    if (longestMatcher != null) {
    	return longestMatcher.match(parsingState);
    }
    
    throw RecognitionExceptionImpl.create();
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    for (Matcher matcher : matchers) {
      matcher.setParentRule(parentRule);
    }
  }

  public String toString() {
    StringBuilder expr = new StringBuilder("longestOne(");
    for (int i = 0; i < matchers.length; i++) {
      expr.append(matchers[i]);
      if (i < matchers.length - 1) {
        expr.append(", ");
      }
    }
    expr.append(")");
    return expr.toString();
  }

}
