/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import org.hamcrest.Matcher;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.TokenType;

public class LexerMatchers {

  public final static Matcher<LexerOutput> hasToken(String tokenValue, TokenType tokenType) {
    return new HasTokenMatcher(tokenValue, tokenType);
  }

  public final static Matcher<LexerOutput> hasLastToken(String tokenValue, TokenType tokenType) {
    return new HasLastTokenMatcher(tokenValue, tokenType);
  }

  public final static Matcher<LexerOutput> hasComment(String commentValue) {
    return new HasCommentMatcher(commentValue);
  }

  public final static Matcher<LexerOutput> hasComment(String commentValue, int commentLine) {
    return new HasCommentMatcher(commentValue, commentLine);
  }
}
