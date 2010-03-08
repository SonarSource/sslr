/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.lexer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexingState {

  private String fileName = null;

  private Map<String, TokenType> keywords;

  private List<Token> comments = new ArrayList<Token>();

  private Charset charset = Charset.defaultCharset();

  private Preprocessor[] preprocessors = new Preprocessor[0];

  public LexingState() {
  }

  public void setPreprocessors(Preprocessor... preprocessors) {
    this.preprocessors = preprocessors;
  }

  final Preprocessor[] getPreprocessors() {
    return preprocessors;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  public Charset getCharset() {
    return charset;
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

  public void startLexing() {
    comments.clear();
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.startLexing();
    }
  }

  public List<Token> getComments() {
    return comments;
  }

  public void endLexing(List<Token> tokens) {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.endLexing(tokens);
    }
  }
}
