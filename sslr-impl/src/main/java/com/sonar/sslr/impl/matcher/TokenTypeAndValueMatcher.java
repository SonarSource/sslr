/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class TokenTypeAndValueMatcher extends TokenMatcher {

  private final TokenType type;
  private final String value;

  public TokenTypeAndValueMatcher(TokenType type, String value) {
    this(type, value, false);
  }

  public TokenTypeAndValueMatcher(TokenType type, String value, boolean hasToBeSkippedFromAst) {
    super(hasToBeSkippedFromAst);
    this.type = type;
    this.value = value;
  }
  
  public String toString() {
    return "TokenTypeValueMatcher(" + type.getName() + ", " + value + ")";
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return type == token.getType() && value.equals(token.getValue());
  }
  
}
