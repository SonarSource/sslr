/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.HashSet;
import java.util.Set;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class TokenTypesMatcher extends TokenMatcher {

  private final Set<TokenType> tokenTypes = new HashSet<TokenType>();

  public TokenTypesMatcher(TokenType... types) {
    super(false);
    for (TokenType keyword : types) {
      this.tokenTypes.add(keyword);
    }
  }

  public String toString() {
    return "tokenTypesMatcher()";
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return tokenTypes.contains(token.getType());
  }
  
}
