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

}
