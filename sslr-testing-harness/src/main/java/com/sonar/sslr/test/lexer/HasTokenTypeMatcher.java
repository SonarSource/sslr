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

class HasTokenTypeMatcher extends BaseMatcher<List<Token>> {

  private final TokenType type;

  HasTokenTypeMatcher(TokenType type) {
    this.type = type;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof List)) {
      return false;
    }
    List<Token> tokens = (List<Token>) obj;
    for (Token token : tokens) {
      if (token.getType() == type) {
        return true;
      }
    }
    return false;
  }

  public void describeTo(Description desc) {
    desc.appendText("Token('" + type + "')");
  }
}
