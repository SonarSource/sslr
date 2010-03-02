/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

import java.util.List;

public abstract class TokenChannel implements Channel {

  private List<Token> tokens;
  private LexingState lexingState;

  public TokenChannel(List<Token> list, LexingState lexingState) {
    this.tokens = list;
    this.lexingState = lexingState;
  }

  public abstract boolean read(CodeReader code);

  protected void addToken(TokenType tokenType, String value, int linePosition, int columnPosition) {
    Token token = new Token(tokenType, value, linePosition, columnPosition, lexingState.getFileName());
    for (Preprocessor preprocessor : lexingState.getPreprocessors()) {
      if (preprocessor.process(token, tokens)) {
        return;
      }
    }
    tokens.add(token);
  }

  protected Token getLastToken() {
    return tokens.get(tokens.size() - 1);
  }
}
