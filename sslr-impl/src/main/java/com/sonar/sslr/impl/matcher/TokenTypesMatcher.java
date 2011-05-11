/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class TokenTypesMatcher extends TokenMatcher {

  private final Set<TokenType> tokenTypes = new HashSet<TokenType>();
  private final TokenType tokenTypesArray[];

  public TokenTypesMatcher(TokenType... types) {
    super(false);
    for (TokenType keyword : types) {
      this.tokenTypes.add(keyword);
    }
    tokenTypesArray = types;
  }
  
  @Override
  public String getDefinition(boolean isRoot) {
  	StringBuilder expr = new StringBuilder("isOneOfThem(");
    for (int i = 0; i < tokenTypesArray.length; i++) {
      expr.append(tokenTypesArray[i].getName());
      if (i < tokenTypesArray.length - 1) {
        expr.append(", ");
      }
    }
    expr.append(")");
    return expr.toString();
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return tokenTypes.contains(token.getType());
  }
  
}
