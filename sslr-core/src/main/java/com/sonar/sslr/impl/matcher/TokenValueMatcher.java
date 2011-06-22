/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;

public class TokenValueMatcher extends TokenMatcher {

  private final String tokenValue;

	protected TokenValueMatcher(String tokenValue) {
    this(tokenValue, false);
  }

  protected TokenValueMatcher(String tokenValue, boolean hasToBeSkippedFromAst) {
    super(hasToBeSkippedFromAst);
    this.tokenValue = tokenValue;
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return tokenValue.hashCode() == token.getValue().hashCode() && tokenValue.equals(token.getValue());
  }
  
  @Override
  public String toString() {
  	return "\"" + tokenValue.replace("\"", "\\\"") + "\"";
  }

}
