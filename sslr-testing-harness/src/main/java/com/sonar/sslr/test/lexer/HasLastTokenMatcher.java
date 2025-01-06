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

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

class HasLastTokenMatcher extends BaseMatcher<List<Token>> {

  private final String tokenValue;
  private final TokenType tokenType;

  HasLastTokenMatcher(String tokenValue, TokenType tokenType) {
    this.tokenType = tokenType;
    this.tokenValue = tokenValue;
  }

  @Override
  public boolean matches(Object obj) {
    if (!(obj instanceof List)) {
      return false;
    }
    List<Token> tokens = (List<Token>) obj;
    if (tokens.isEmpty()) {
      throw new IllegalArgumentException("There must be at least one lexed token.");
    }
    Token lastToken = tokens.get(tokens.size() - 1);
    return lastToken.getValue().equals(tokenValue) && lastToken.getType() == tokenType;
  }

  @Override
  public void describeTo(Description desc) {
    desc.appendText("Token('" + tokenValue + "'," + tokenType + ")");
  }

}
