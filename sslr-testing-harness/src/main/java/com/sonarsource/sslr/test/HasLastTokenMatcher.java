/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonarsource.sslr.api.Token;
import com.sonarsource.sslr.api.TokenType;
import com.sonarsource.sslr.impl.LexerOutput;

class HasLastTokenMatcher extends BaseMatcher<LexerOutput> {

  private final String tokenValue;
  private final TokenType tokenType;

  HasLastTokenMatcher(String tokenValue, TokenType tokenType) {
    this.tokenType = tokenType;
    this.tokenValue = tokenValue;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof LexerOutput)) {
      return false;
    }
    LexerOutput output = (LexerOutput) obj;
    Token lastToken = output.getLastToken();
    if (lastToken.getValue().equals(tokenValue) && lastToken.getType() == tokenType) {
      return true;
    } else {
      return false;
    }

  }

  public void describeTo(Description desc) {
    desc.appendText("Token('" + tokenValue + "'," + tokenType + ")");
  }
}
