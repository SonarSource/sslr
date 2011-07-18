/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;

public class BooleanMatcher extends TokenMatcher {

  private final boolean internalState;

  protected BooleanMatcher(boolean internalState) {
    super(false);

    this.internalState = internalState;
  }

	@Override
	protected boolean isExpectedToken(Token token) {
		return internalState;
	}

  @Override
  public String toString() {
    return (internalState) ? "isTrue()" : "isFalse()";
  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().hashCode();
		result = prime * result + (internalState ? 1231 : 1237);
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
		BooleanMatcher other = (BooleanMatcher) obj;
		if (internalState != other.internalState)
			return false;
		return true;
	}

}
