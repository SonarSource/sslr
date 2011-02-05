/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.calculator;

import com.sonar.sslr.dsl.DslMemory;

public class VariableExpression implements AbstractExpression {

  private String variableName;
  private DslMemory memory;

  public void setVariableName(String name) {
    this.variableName = name;
  }

  public void setMemory(DslMemory memory) {
    this.memory = memory;
  }

  public double value() {
    return (Double) memory.get(variableName);
  }
}
