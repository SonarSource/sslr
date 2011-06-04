/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import com.sonar.sslr.dsl.DslContext;

public class VariableExpression implements AbstractExpression {

  private String variableName;
  private DslContext memory;

  public VariableExpression(DslContext memory) {
    this.memory = memory;
  }

  public void addIdentifier(String name) {
    this.variableName = name;
  }

  public double value() {
    return (Double) memory.get(variableName);
  }
}
