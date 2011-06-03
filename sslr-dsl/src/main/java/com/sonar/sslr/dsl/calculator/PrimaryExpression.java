/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

public class PrimaryExpression implements AbstractExpression {

  private double value;

  public void addIntValue(Integer value) {
    this.value = value;
  }

  public void addDoubleValue(Double value) {
    this.value = value;
  }

  public double value() {
    return value;
  }
}
