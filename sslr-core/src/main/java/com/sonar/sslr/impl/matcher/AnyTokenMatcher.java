/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;

public class AnyTokenMatcher extends TokenMatcher {

  protected AnyTokenMatcher() {
    super(false);
  }

  @Override
  protected final boolean isExpectedToken(Token token) {
    return true;
  }

  @Override
  public String toString() {
    return "anyToken()";
  }

}
