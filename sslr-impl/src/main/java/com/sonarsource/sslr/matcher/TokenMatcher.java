/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.RecognitionExceptionImpl;
import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.Token;

public abstract class TokenMatcher extends Matcher {

  private final boolean hasToBeSkippedFromAst;;

  public TokenMatcher(boolean hasToBeSkippedFromAst) {
    this.hasToBeSkippedFromAst = hasToBeSkippedFromAst;
  }

  public AstNode match(ParsingState parsingState) {
    if (isExpectedToken(parsingState.peekToken(parsingState.lexerIndex, this))) {
      Token token = parsingState.popToken(this);
      if (hasToBeSkippedFromAst) {
        return null;
      } else {
        return new AstNode(token);
      }
    } else {
      throw RecognitionExceptionImpl.create();
    }
  }

  protected abstract boolean isExpectedToken(Token token);

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
  }
}
