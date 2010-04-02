/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;

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
