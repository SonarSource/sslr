/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

public class ConditionPrinter {

  private Condition condition;
  private StringBuilder output;

  public void setOutput(StringBuilder output) {
    this.output = output;
  }

  public void setValue(Condition condition) {
    this.condition = condition;
  }

  public void execute() {
    output.append(condition.value());
  }

}
