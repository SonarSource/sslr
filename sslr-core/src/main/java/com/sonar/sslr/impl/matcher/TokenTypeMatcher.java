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
  
  public TokenType getType() {
    return type;
  }

  @Override
  protected final boolean isExpectedToken(Token token) {
    return type == token.getType();
  }

  @Override
  public String toString() {
    return type.getName();
  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenTypeMatcher other = (TokenTypeMatcher) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
