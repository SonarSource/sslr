/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

public class Literal {

  private String value;

  public Literal(String value) {
    this.value = value.substring(1, value.length() - 1);
  }

  public String toString() {
    return value;
  }
}
