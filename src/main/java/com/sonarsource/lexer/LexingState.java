/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.lexer;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class LexingState {

  private String fileName = null;

  private Map<String, TokenType> keywords;

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
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.startLexing();
    }
  }

  public void endLexing(List<Token> tokens) {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.endLexing(tokens);
    }
  }
}
