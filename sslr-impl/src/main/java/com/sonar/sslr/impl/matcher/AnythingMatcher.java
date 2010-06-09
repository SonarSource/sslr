/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.Token;

public class AnythingMatcher extends TokenMatcher {

  public AnythingMatcher() {
    super(false);
  }

  @Override
  protected boolean isExpectedToken(Token token) {
    return true;
  }
  
  public String toString() {
    return "anyThingMatcher()";
  }

}
