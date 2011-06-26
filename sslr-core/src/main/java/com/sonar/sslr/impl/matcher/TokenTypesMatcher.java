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
  private final TokenType tokenTypesArray[];

  protected TokenTypesMatcher(TokenType... types) {
    super(false);
    for (TokenType keyword : types) {
      this.tokenTypes.add(keyword);
    }
    tokenTypesArray = types;
  }

  @Override
  protected final boolean isExpectedToken(Token token) {
    return tokenTypes.contains(token.getType());
  }

  @Override
  public String toString() {
    return "isOneOfThem";
  }

}
