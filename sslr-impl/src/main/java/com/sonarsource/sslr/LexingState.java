/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sonarsource.sslr.api.Comments;
import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.api.TokenType;

public class LexingState {

  private File file = null;

  private Map<String, TokenType> keywords;

  private Map<Integer, Token> comments = new HashMap<Integer, Token>();

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

  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
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

  public Comments getComments() {
    return new Comments(comments);
  }

  public Map<Integer, Token> getCommentTokens() {
    return comments;
  }

  public void endLexing(List<Token> tokens) {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.endLexing(tokens);
    }
  }
}
