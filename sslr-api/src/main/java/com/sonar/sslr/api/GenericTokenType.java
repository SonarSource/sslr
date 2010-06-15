/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import com.sonar.sslr.api.TokenType;

public enum GenericTokenType implements TokenType {
  COMMENT, IDENTIFIER, LITERAL, CONSTANT, EOF, EOL;

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
