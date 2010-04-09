/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.impl;

import com.sonarsource.sslr.api.TokenType;

public enum MockTokenType implements TokenType {

  WORD;

  public String getName() {
    return "WORD";
  }

  public String getValue() {
    return "WORD";
  }

  public boolean hasToBeSkippedFromAst() {
    return false;
  }
}