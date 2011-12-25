/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode.controlflow;

import com.sonar.sslr.dsl.bytecode.ConditionalBlockInstruction;
import com.sonar.sslr.dsl.condition.Condition;

public class If implements ConditionalBlockInstruction {

  private Condition condition;

  public void add(Condition condition) {
    this.condition = condition;
  }

  public boolean shouldExecuteConditionalBlock() {
    return condition.value();
  }
}
