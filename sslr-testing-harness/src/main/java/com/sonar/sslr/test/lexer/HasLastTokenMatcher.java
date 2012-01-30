/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

class HasLastTokenMatcher extends BaseMatcher<List<Token>> {

  private final String tokenValue;
  private final TokenType tokenType;

  HasLastTokenMatcher(String tokenValue, TokenType tokenType) {
    this.tokenType = tokenType;
    this.tokenValue = tokenValue;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof List)) {
      return false;
    }
    List<Token> tokens = (List<Token>) obj;
    if (tokens.size() == 0) {
      throw new IllegalArgumentException("There must be at least one lexed token.");
    }
    Token lastToken = tokens.get(tokens.size() - 1);
    return lastToken.getValue().equals(tokenValue) && lastToken.getType() == tokenType;
  }

  public void describeTo(Description desc) {
    desc.appendText("Token('" + tokenValue + "'," + tokenType + ")");
  }
}
