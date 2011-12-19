/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import org.hamcrest.Matcher;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public final class LexerMatchers {

  private LexerMatchers() {
  }

  public static Matcher<LexerOutput> hasToken(String tokenValue, TokenType tokenType) {
    return new HasTokenMatcher(tokenValue, tokenType);
  }

  public static Matcher<LexerOutput> hasToken(String tokenValue) {
    return new HasTokenValueMatcher(tokenValue);
  }

  public static Matcher<LexerOutput> hasOriginalToken(String tokenValue) {
    return new HasTokenValueMatcher(tokenValue, true);
  }

  public static Matcher<LexerOutput> hasToken(TokenType tokenType) {
    return new HasTokenTypeMatcher(tokenType);
  }

  public static Matcher<LexerOutput> hasTokens(String... tokenValues) {
    return new HasTokensMatcher(tokenValues);
  }

  public static Matcher<LexerOutput> hasLastToken(String tokenValue, TokenType tokenType) {
    return new HasLastTokenMatcher(tokenValue, tokenType);
  }

  public static Matcher<LexerOutput> hasComment(String commentValue) {
    return new HasCommentMatcher(commentValue);
  }

  public static Matcher<LexerOutput> hasComment(String commentValue, int commentLine) {
    return new HasCommentMatcher(commentValue, commentLine);
  }

  public static Matcher<LexerOutput> hasOriginalComment(String commentValue) {
    return new HasCommentMatcher(commentValue, true);
  }

  public static Matcher<LexerOutput> hasOriginalComment(String commentValue, int commentLine) {
    return new HasCommentMatcher(commentValue, commentLine, true);
  }

}
