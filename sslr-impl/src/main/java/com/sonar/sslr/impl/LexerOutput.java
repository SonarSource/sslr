/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sonar.sslr.api.Comments;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class LexerOutput {

  private List<Token> tokens = new ArrayList<Token>();
  private Map<Integer, Token> comments = new HashMap<Integer, Token>();
  private File file = null;
  private final Preprocessor[] preprocessors;
  private Map<String, TokenType> keywords;

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

  public Token getLastToken() {
    return tokens.get(tokens.size() - 1);
  }

  public void addToken(TokenType tokenType, String value, int linePosition, int columnPosition) {
    Token token = new Token(tokenType, value, linePosition, columnPosition);
    if (file != null) {
      token.setFile(file);
    }
    for (Preprocessor preprocessor : preprocessors) {
      if (preprocessor.process(token, this)) {
        return;
      }
    }
    tokens.add(token);
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
    comments.put(token.getLine(), token);
  }

  public Map<Integer, Token> getCommentTokens() {
    return comments;
  }

  public Token get(int i) {
    return tokens.get(i);
  }

  public void setKeywords(Map<String, TokenType> keywords) {
    this.keywords = keywords;
  }

  public boolean isKeyword(String key) {
    return keywords.containsKey(key);
  }

  public TokenType getKeyword(String key) {
    return keywords.get(key);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(size()).append(" tokens");
    if (size() > 0) {
      Token lastToken = getLastToken();
      result.append(", last one is Token('" + lastToken.getValue() + "'");
      result.append(", " + lastToken.getType() + ")");
    }
    return result.toString();
  }

  public void addAllTokens(List<Token> allNewtokens) {
    tokens.addAll(allNewtokens);
  }
}
