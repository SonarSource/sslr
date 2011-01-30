/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.xpath.operations.String;
import org.junit.Test;

public class BytecodeTest {

  Bytecode bytecode = new Bytecode();

  @Test
  public void shoudAddAndExecuteInstruction() {
    MyInstruction myInstruction = new MyInstruction();
    bytecode.addInstruction(myInstruction);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(true));
  }

  @Test
  public void shoudNotThrowExceptionWhenAddingNonInstruction() {
    bytecode.addInstruction(new String());
    bytecode.execute();
  }

  private class MyInstruction {

    private boolean hasBeenExecuted = false;

    public void execute() {
      hasBeenExecuted = true;
    }
  }

}
