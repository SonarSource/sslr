/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import java.util.List;

import org.hamcrest.Matcher;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public final class LexerMatchers {

  private LexerMatchers() {
  }

  public static Matcher<List<Token>> hasToken(String tokenValue, TokenType tokenType) {
    return new HasTokenMatcher(tokenValue, tokenType);
  }

  public static Matcher<List<Token>> hasToken(String tokenValue) {
    return new HasTokenValueMatcher(tokenValue);
  }

  public static Matcher<List<Token>> hasOriginalToken(String tokenValue) {
    return new HasTokenValueMatcher(tokenValue, true);
  }

  public static Matcher<List<Token>> hasToken(TokenType tokenType) {
    return new HasTokenTypeMatcher(tokenType);
  }

  public static Matcher<List<Token>> hasTokens(String... tokenValues) {
    return new HasTokensMatcher(tokenValues);
  }

  public static Matcher<List<Token>> hasLastToken(String tokenValue, TokenType tokenType) {
    return new HasLastTokenMatcher(tokenValue, tokenType);
  }

  public static Matcher<List<Token>> hasComment(String commentValue) {
    return new HasCommentMatcher(commentValue);
  }

  public static Matcher<List<Token>> hasComment(String commentValue, int commentLine) {
    return new HasCommentMatcher(commentValue, commentLine);
  }

  public static Matcher<List<Token>> hasOriginalComment(String commentValue) {
    return new HasCommentMatcher(commentValue, true);
  }

  public static Matcher<List<Token>> hasOriginalComment(String commentValue, int commentLine) {
    return new HasCommentMatcher(commentValue, commentLine, true);
  }

}
