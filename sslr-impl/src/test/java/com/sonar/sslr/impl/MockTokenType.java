/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import com.sonar.sslr.api.TokenType;

public enum MockTokenType implements TokenType {

  WORD, WORD1, WORD2;

  public String getName() {
    return name();
  }

  public String getValue() {
    return name();
  }

  public boolean hasToBeSkippedFromAst() {
    return false;
  }
}
