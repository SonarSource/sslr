/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

public class Calculator implements AbstractExpression {

  private AbstractExpression value;

  public void add(AbstractExpression value) {
    this.value = value;
  }

  public double value() {
    return value.value();
  }
}
