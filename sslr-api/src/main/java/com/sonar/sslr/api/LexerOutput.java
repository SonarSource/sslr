/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexerOutput {

  private List<Token> tokens = new ArrayList<Token>();
  private List<Token> preprocessingTokens = new ArrayList<Token>();
  private Map<Integer, Token> comments = new HashMap<Integer, Token>();
  private File file = null;
  private final Preprocessor[] preprocessors;

  public LexerOutput(Preprocessor... preprocessors) {
    this.preprocessors = preprocessors;
  }

  public LexerOutput(List<Token> tokens) {
    this.tokens = tokens;
    this.preprocessors = null;
  }

  public List<Token> getTokens() {
    return tokens;
  }

  public List<Token> getPreprocessingTokens() {
    return preprocessingTokens;
  }

  public Token getLastToken() {
    return tokens.get(tokens.size() - 1);
  }

  public void removeLastTokens(int numberOfTokensToRemove) {
    for (int i = 0; i < numberOfTokensToRemove; i++) {
      tokens.remove(tokens.size() - 1);
    }
  }

  public void addTokenAndProcess(TokenType tokenType, String value, int linePosition, int columnPosition) {
    Token token = new Token(tokenType, value, linePosition, columnPosition);
    if (file != null) {
      token.setFile(file);
    }
    for (Preprocessor preprocessor : preprocessors) {
      if (preprocessor.process(token, this)) {
        return;
      }
    }
    addToken(token);
  }

  public void addPreprocessingToken(Token token) {
    if (file != null) {
      token.setFile(file);
    }
    preprocessingTokens.add(token);
  }

  public void addToken(Token token) {
    tokens.add(token);
  }

  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public int size() {
    return tokens.size();
  }

  public Comments getComments() {
    return new Comments(comments);
  }

  public void addCommentToken(Token token) {
    if (file != null) {
      token.setFile(file);
    }
    comments.put(token.getLine(), token);
  }

  public Map<Integer, Token> getCommentTokens() {
    return comments;
  }

  public Token get(int i) {
    return tokens.get(i);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(size()).append(" tokens ");
    for (Token token : tokens) {
      result.append("('" + token.getValue() + "' ");
      result.append(": " + token.getType() + ")");
    }
    return result.toString();
  }

  public void addAllTokens(List<Token> allNewtokens) {
    tokens.addAll(allNewtokens);
  }

  public void setTokens(List<Token> tokens) {
    this.tokens = tokens;
  }
}
