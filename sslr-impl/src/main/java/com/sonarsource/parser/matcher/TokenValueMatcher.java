/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.sslr.api.Token;

public class TokenValueMatcher extends TokenMatcher {

  private String tokenValue;

  public TokenValueMatcher(String tokenValue) {
    this(tokenValue, false);
  }

  public TokenValueMatcher(String tokenValue, boolean hasToBeSkippedFromAst) {
    super(hasToBeSkippedFromAst);
    this.tokenValue = tokenValue;
  }

  public String toString() {
    return tokenValue;
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return tokenValue.hashCode() == token.getValue().hashCode() && tokenValue.equals(token.getValue());
  }

}
