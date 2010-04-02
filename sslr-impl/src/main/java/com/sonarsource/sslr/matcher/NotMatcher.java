/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.RecognitionException;
import com.sonarsource.sslr.ast.AstNode;

public class NotMatcher extends Matcher {

  private Matcher matcher;

  public NotMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    if (matcher.isMatching(parsingState)) {
      throw RecognitionException.create();
    } else {
      return new AstNode(parsingState.popToken(this));
    }
  }

  @Override
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  public String toString() {
    return "(" + matcher + ")!";
  }
}
