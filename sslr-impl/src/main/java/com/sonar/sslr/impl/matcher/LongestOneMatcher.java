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

  public LongestOneMatcher(Matcher... matchers) {
  	super(matchers);
  }

  public AstNode match(ParsingState parsingState) {
  	Matcher longestMatcher = null;
  	int longestMatchIndex = -1;
  	
    for (Matcher matcher : super.children) {
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

  public String toString() {
    StringBuilder expr = new StringBuilder("longestOne(");
    for (int i = 0; i < super.children.length; i++) {
      expr.append(super.children[i]);
      if (i < super.children.length - 1) {
        expr.append(", ");
      }
    }
    expr.append(")");
    return expr.toString();
  }

}
