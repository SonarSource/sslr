/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

public enum DefaultDslTokenType implements TokenType {

  WORD, LITERAL, INTEGER, PUNCTUATOR, DOUBLE;

  public String getName() {
    return name();
  }

  public String getValue() {
    return name();
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }

  public String toString() {
    return name();
  }
}
