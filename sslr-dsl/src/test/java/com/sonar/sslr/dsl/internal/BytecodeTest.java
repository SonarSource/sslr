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

import com.sonar.sslr.dsl.adapter.ConditionalBlockAdapter;
import com.sonar.sslr.dsl.adapter.ExecutableAdapter;
import com.sonar.sslr.dsl.adapter.LoopBlockAdapter;

public class BytecodeTest {

  Bytecode bytecode = new Bytecode();

  @Test
  public void shoudExecuteAdapter() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    bytecode.addAdapter(myInstruction);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(true));
  }

  @Test
  public void shoudExecuteConditionalBlock() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    TrueConditionalBlockAdapter trueConditionalBlock = new TrueConditionalBlockAdapter();
    bytecode.startControlFlowAdapter(trueConditionalBlock);
    bytecode.addAdapter(myInstruction);
    bytecode.endControlFlowAdapter(trueConditionalBlock);
    bytecode.addAdapter(trueConditionalBlock);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(true));
  }

  @Test
  public void shoudNotExecuteConditionalBlock() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    FalseConditionalBlockAdapter falseConditionalBlock = new FalseConditionalBlockAdapter();
    bytecode.startControlFlowAdapter(falseConditionalBlock);
    bytecode.addAdapter(myInstruction);
    bytecode.endControlFlowAdapter(falseConditionalBlock);
    bytecode.addAdapter(falseConditionalBlock);
    bytecode.execute();

    assertThat(myInstruction.hasBeenExecuted, is(false));
  }

  @Test
  public void shoudExecuteLoopBlockThreeTimes() {
    MyExecutableAdapter myInstruction = new MyExecutableAdapter();
    ThreeLoopBlockAdapter loopBlock = new ThreeLoopBlockAdapter();
    bytecode.startControlFlowAdapter(loopBlock);
    bytecode.addAdapter(myInstruction);
    bytecode.endControlFlowAdapter(loopBlock);
    bytecode.addAdapter(loopBlock);
    bytecode.execute();

    assertThat(myInstruction.numberOfExecution, is(3));
  }

  @Test
  public void shoudNotThrowExceptionWhenAddingNonInstruction() {
    bytecode.addAdapter(new String());
    bytecode.execute();
  }

  private class MyExecutableAdapter implements ExecutableAdapter {

    private boolean hasBeenExecuted = false;
    private int numberOfExecution = 0;

    public void execute() {
      hasBeenExecuted = true;
      numberOfExecution++;
    }
  }

  private class TrueConditionalBlockAdapter implements ConditionalBlockAdapter {

    public boolean shouldExecuteConditionalBlock() {
      return true;
    }
  }

  private class FalseConditionalBlockAdapter implements ConditionalBlockAdapter {

    public boolean shouldExecuteConditionalBlock() {
      return false;
    }
  }

  private class ThreeLoopBlockAdapter implements LoopBlockAdapter {

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
