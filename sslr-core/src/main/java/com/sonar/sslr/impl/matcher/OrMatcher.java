/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingEvent;

public class OrMatcher extends MemoizedMatcher {

  protected OrMatcher(Matcher... matchers) {
    super(matchers);
  }

  public AstNode matchWorker(ParsingState parsingState) {
    for (Matcher matcher : super.children) {
      if (matcher.isMatching(parsingState)) {
        return matcher.match(parsingState);
      }
    }
    throw BacktrackingEvent.create();
  }
  
  @Override
  public String toString() {
  	return "or";
  }

}
