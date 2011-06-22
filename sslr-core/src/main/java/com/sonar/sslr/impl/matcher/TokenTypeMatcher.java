/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;

public class TokenTypeMatcher extends TokenMatcher {

  private final TokenType type;

	protected TokenTypeMatcher(TokenType type) {
    this(type, false);
  }

  protected TokenTypeMatcher(TokenType type, boolean hasToBeSkippedFromAst) {
    super(hasToBeSkippedFromAst);
    this.type = type;
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return type == token.getType();
  }
  
  @Override
  public String toString() {
  	return type.getName();
  }
  
}
