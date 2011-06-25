/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;

public abstract class TokenMatcher extends NonMemoizedMatcher {

  private final boolean hasToBeSkippedFromAst;

  protected TokenMatcher(boolean hasToBeSkippedFromAst) {
    this.hasToBeSkippedFromAst = hasToBeSkippedFromAst;
  }

  public AstNode matchWorker(ParsingState parsingState) {
    if (isExpectedToken(parsingState.peekToken(parsingState.lexerIndex, this))) {
      Token token = parsingState.popToken(this);
      if (hasToBeSkippedFromAst) {
        return null;
      } else {
        return new AstNode(token);
      }
    } else {
      throw BacktrackingException.create();
    }
  }

  protected abstract boolean isExpectedToken(Token token);
  
}
