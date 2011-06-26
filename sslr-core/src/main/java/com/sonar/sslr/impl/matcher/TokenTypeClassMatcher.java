/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;

public class TokenTypeClassMatcher extends TokenMatcher {

  private final Class typeClass;

	protected TokenTypeClassMatcher(Class typeClass) {
    super(false);
    this.typeClass = typeClass;
  }

  @Override
  protected final boolean isExpectedToken(Token token) {
    return typeClass == token.getType().getClass();
  }
  
  @Override
  public String toString() {
  	return typeClass.getCanonicalName() + ".class";
  }
  
}
