/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public abstract class TokenMatcher extends Matcher {

  private final boolean hasToBeSkippedFromAst;

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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void endParsing(ParsingState parsingState) {    
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void startParsing(ParsingState parsingState) {    
  }
}
