/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.preprocessor;

import com.sonar.sslr.api.CharacterTokenType;

public enum MyCharacter implements CharacterTokenType {
  EQUAL("="), EXCLAMATION("!"), COMMA(",");

  private final String value;

  private MyCharacter(String word) {
    this.value = word;
  }

  public String getName() {
    return name();
  }

  public String getValue() {
    return value;
  }

  public boolean hasToBeSkippedFromAst() {
    return false;
  }

  public char getChar() {
    return value.charAt(0);
  }

}
