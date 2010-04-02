/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.impl.ParsingState;
import com.sonarsource.sslr.impl.RecognitionExceptionImpl;

public class NotMatcher extends Matcher {

  private Matcher matcher;

  public NotMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    if (matcher.isMatching(parsingState)) {
      throw RecognitionExceptionImpl.create();
    } else {
      return new AstNode(parsingState.popToken(this));
    }
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  public String toString() {
    return "(" + matcher + ")!";
  }
}
