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
  
  public String getTokenValue() {
    return tokenValue;
  }

  @Override
  protected final boolean isExpectedToken(Token token) {
    return tokenValue.hashCode() == token.getValue().hashCode() && tokenValue.equals(token.getValue());
  }

  @Override
  public String toString() {
    return "\"" + tokenValue.replace("\"", "\\\"") + "\"";
  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().hashCode();
		result = prime * result + ((tokenValue == null) ? 0 : tokenValue.hashCode());
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
		TokenValueMatcher other = (TokenValueMatcher) obj;
		if (tokenValue == null) {
			if (other.tokenValue != null)
				return false;
		} else if (!tokenValue.equals(other.tokenValue))
			return false;
		return true;
	}

}
