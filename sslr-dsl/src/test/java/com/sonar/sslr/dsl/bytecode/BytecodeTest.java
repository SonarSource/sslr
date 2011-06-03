/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.bytecode;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.xpath.operations.String;
import org.junit.Test;

import com.sonar.sslr.dsl.bytecode.Bytecode;
import com.sonar.sslr.dsl.bytecode.ConditionalBlockInstruction;
import com.sonar.sslr.dsl.bytecode.ExecutableInstruction;
import com.sonar.sslr.dsl.bytecode.LoopBlockInstruction;

public class BytecodeTest {

  Bytecode bytecode = new Bytecode();

  @Test
  public void shoudExecuteAdapter() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    bytecode.addInstruction(myInstruction);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(true));
  }

  @Test
  public void shoudExecuteConditionalBlock() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    TrueConditionalBlockAdapter trueConditionalBlock = new TrueConditionalBlockAdapter();
    bytecode.startControlFlowInstruction(trueConditionalBlock);
    bytecode.addInstruction(myInstruction);
    bytecode.endControlFlowInstruction(trueConditionalBlock);
    bytecode.addInstruction(trueConditionalBlock);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(true));
  }

  @Test
  public void shoudNotExecuteConditionalBlock() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    FalseConditionalBlockAdapter falseConditionalBlock = new FalseConditionalBlockAdapter();
    bytecode.startControlFlowInstruction(falseConditionalBlock);
    bytecode.addInstruction(myInstruction);
    bytecode.endControlFlowInstruction(falseConditionalBlock);
    bytecode.addInstruction(falseConditionalBlock);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(false));
  }

  @Test
  public void shoudExecuteLoopBlockThreeTimes() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    ThreeLoopBlockAdapter loopBlock = new ThreeLoopBlockAdapter();
    bytecode.startControlFlowInstruction(loopBlock);
    bytecode.addInstruction(myInstruction);
    bytecode.endControlFlowInstruction(loopBlock);
    bytecode.addInstruction(loopBlock);
    bytecode.execute();

    assertThat(myInstruction.numberOfExecution, is(3));
  }

  @Test
  public void shoudNotThrowExceptionWhenAddingNonInstruction() {
    bytecode.addInstruction(new String());
    bytecode.execute();
  }

  private class MyExecutableAdapter implements ExecutableInstruction {

    private boolean hasBeenExecuted = false;
    private int numberOfExecution = 0;

    public void execute() {
      hasBeenExecuted = true;
      numberOfExecution++;
    }
  }

  private class TrueConditionalBlockAdapter implements ConditionalBlockInstruction {

    public boolean shouldExecuteConditionalBlock() {
      return true;
    }
  }

  private class FalseConditionalBlockAdapter implements ConditionalBlockInstruction {

    public boolean shouldExecuteConditionalBlock() {
      return false;
    }
  }

  private class ThreeLoopBlockAdapter implements LoopBlockInstruction {

    int pendingLoop;

    public boolean shouldExecuteLoopBlockIteration() {
      pendingLoop--;
      return pendingLoop > -1;
    }

    public void initLoopState() {
      pendingLoop = 3;
    }

  }

}
