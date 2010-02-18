/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.lexer.Token;
import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;

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
      throw RecognitionException.create();
    }
  }

  protected abstract boolean isExpectedToken(Token token);

  @Override
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
  }
}
