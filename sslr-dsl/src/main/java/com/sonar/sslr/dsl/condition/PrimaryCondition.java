/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

public class PrimaryCondition implements AbstractCondition {

  public boolean value;

  public void setBoolean(String value) {
    this.value = Boolean.parseBoolean(value);
  }

  public boolean value() {
    return value;
  }
}
