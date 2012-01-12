/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public final class LexerOutput {

  private File file = null;
  private List<Token> tokens = new ArrayList<Token>(1000);
  private final List<Token> preprocessingTokens = new ArrayList<Token>();
  private final ListMultimap<Integer, Token> comments = LinkedListMultimap.<Integer, Token> create();
  private final Preprocessor[] preprocessors;
  private final List<Token> currentTokenTrivia = Lists.newLinkedList();

  public LexerOutput(Preprocessor... preprocessors) {
    this.preprocessors = preprocessors;
  }

  public LexerOutput(List<Token> tokens) {
    addAllTokens(tokens);
    this.preprocessors = null;
  }

  public List<Token> getTokens() {
    return tokens;
  }

  public List<Token> getPreprocessingTokens() {
    return preprocessingTokens;
  }

  public Token getLastToken() {
    if (size() > 0) {
      return tokens.get(tokens.size() - 1);
    }
    return null;
  }

  public Token getFirstToken() {
    if (size() > 0) {
      return tokens.get(0);
    }
    return null;
  }

  public void removeLastTokens(int numberOfTokensToRemove) {
    int desiredTokenListLength = tokens.size() - numberOfTokensToRemove;
    List<Token> oldCurrentTokenTrivia = Lists.newLinkedList(currentTokenTrivia);
    currentTokenTrivia.clear();
    while (tokens.size() > desiredTokenListLength) {
      Token removedToken = tokens.remove(desiredTokenListLength);
      currentTokenTrivia.addAll(removedToken.getTrivia());
    }
    currentTokenTrivia.addAll(oldCurrentTokenTrivia);
  }

  /**
   * Add a new token and notify the preprocessors
   * 
   * @param tokenType
   * @param value
   * @param linePosition
   * @param columnPosition
   */
  public void addTokenAndProcess(TokenType tokenType, String value, int linePosition, int columnPosition) {
    addTokenAndProcess(tokenType, value, value, linePosition, columnPosition);
  }

  /**
   * Add a new token and notify the preprocessors
   * 
   * @param tokenType
   * @param value
   * @param originalValue
   * @param linePosition
   * @param columnPosition
   */
  public void addTokenAndProcess(TokenType tokenType, String value, String originalValue, int linePosition, int columnPosition) {
    Token token = new Token(tokenType, value, originalValue, linePosition, columnPosition, file);
    for (Preprocessor preprocessor : preprocessors) {
      if (preprocessor.process(token, this)) {
        return;
      }
    }
    addToken(token);
  }

  /**
   * This method must be called by a preprocessor when a token has been temporary consumed by this preprocessor but finally must be pushed
   * back to the LexerOutput. With this method all other preprocessors will be notified to optionally consumed this token.
   */
  public void pushBackTokenAndProcess(Token token, Preprocessor preprocessorToExclude) {
    for (Preprocessor preprocessor : preprocessors) {
      if (preprocessor != preprocessorToExclude && preprocessor.process(token, this)) {
        return;
      }
    }
    addToken(token);
  }

  /**
   * This method must be called by a preprocessor when some tokens have been temporary consumed by this preprocessor but finally must be
   * pushed back to the LexerOutput. With this method all other preprocessors will be notified to optionally consumed those tokens.
   */
  public void pushBackTokensAndProcess(List<Token> tokens, Preprocessor preprocessorToExclude) {
    for (Token token : tokens) {
      pushBackTokenAndProcess(token, preprocessorToExclude);
    }
  }

  public void addPreprocessingToken(Token token) {
    preprocessingTokens.add(token);
    token.setIsPreprocessorTrivia();
    currentTokenTrivia.add(token);
  }

  /**
   * Add a token to the list without notifying preprocessors.
   * 
   * @param token
   */
  public void addToken(Token token) {
    if ( !tokens.isEmpty()) {
      Token previousToken = tokens.get(tokens.size() - 1);
      token.setPreviousToken(previousToken);
      previousToken.setFollowingToken(token);
    }
    token.addAllTrivia(currentTokenTrivia);
    currentTokenTrivia.clear();
    tokens.add(token);
  }

  /**
   * Add a list of tokens to the list without notifying preprocessors.
   * 
   * @param token
   */
  public void addAllTokens(List<Token> allNewtokens) {
    for (Token token : allNewtokens) {
      addToken(token);
    }
  }

  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public String getFileName() {
    if (file != null) {
      return file.getName();
    }
    return null;
  }

  public String getFileAbsolutePath() {
    if (file != null) {
      return file.getAbsolutePath();
    }
    return null;
  }

  public int size() {
    return tokens.size();
  }

  public Comments getComments() {
    return new Comments(comments);
  }

  public Comments getComments(CommentAnalyser commentAnalyser) {
    return new Comments(comments, commentAnalyser);
  }

  public void addCommentToken(Token token) {
    comments.put(token.getLine(), token);
    token.setIsCommentTrivia();
    currentTokenTrivia.add(token);
  }

  public Collection<Token> getCommentTokens() {
    return comments.values();
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

  public void setTokens(List<Token> tokens) {
    this.tokens = tokens;
  }
}
