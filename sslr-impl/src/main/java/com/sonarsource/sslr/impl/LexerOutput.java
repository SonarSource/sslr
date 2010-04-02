/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.impl;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.api.TokenType;

public class LexerOutput {

  private List<Token> tokens = new ArrayList<Token>();
  private LexingState lexingState;

  public LexerOutput(LexingState lexingState) {
    this.lexingState = lexingState;
  }

  public List<Token> getTokens() {
    return tokens;
  }

  public Token getLastToken() {
    return tokens.get(tokens.size() - 1);
  }

  public LexingState getLexingState() {
    return lexingState;
  }

  public void addToken(TokenType tokenType, String value, int linePosition, int columnPosition) {
    Token token = new Token(tokenType, value, linePosition, columnPosition);
    if (lexingState.getFile() != null) {
      token.setFile(lexingState.getFile());
    }
    for (Preprocessor preprocessor : lexingState.getPreprocessors()) {
      if (preprocessor.process(token, tokens)) {
        return;
      }
    }
    tokens.add(token);
  }

  public void addToken(Token token) {
    tokens.add(token);
  }

  public int size() {
    return tokens.size();
  }

  public Token get(int i) {
    return tokens.get(i);
  }
}
