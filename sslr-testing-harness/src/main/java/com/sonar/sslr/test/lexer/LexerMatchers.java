/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
