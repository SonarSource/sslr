/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Token;

class HasTokenValueMatcher extends BaseMatcher<LexerOutput> {

  private final String tokenValue;

  HasTokenValueMatcher(String tokenValue) {
    this.tokenValue = tokenValue;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof LexerOutput)) {
      return false;
    }
    LexerOutput output = (LexerOutput) obj;
    for (Token token : output.getTokens()) {
      if (token.getValue().equals(tokenValue)) {
        return true;
      }
    }
    return false;
  }

  public void describeTo(Description desc) {
    desc.appendText("Token('" + tokenValue + "')");
  }
}
