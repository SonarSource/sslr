/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.condition;

import com.sonar.sslr.dsl.bytecode.ExecutableInstruction;

public class ConditionPrinter implements ExecutableInstruction {

  private Condition condition;
  private StringBuilder output;

  public ConditionPrinter(StringBuilder output) {
    this.output = output;
  }

  public void add(Condition condition) {
    this.condition = condition;
  }

  public void execute() {
    output.append(condition.value());
  }

}
