/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.RecognitionException;
import com.sonarsource.sslr.api.AstNode;

public class BooleanMatcher extends Matcher {

  private final boolean internalState;

  public BooleanMatcher(boolean internalState) {
    this.internalState = internalState;
  }

  public AstNode match(ParsingState parsingState) {
    if (internalState) {
      return new AstNode(this, "trueMatcher", parsingState.popToken(this));
    } else {
      throw RecognitionException.create();
    }
  }

  public String toString() {
    return "" + internalState;
  }

  @Override
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
  }
}
