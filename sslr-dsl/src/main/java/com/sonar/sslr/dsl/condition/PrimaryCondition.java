/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

public class PrimaryCondition implements AbstractCondition {

  public boolean value;

  public void add(Boolean value) {
    this.value = value;
  }

  public boolean value() {
    return value;
  }
}
