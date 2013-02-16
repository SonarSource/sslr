/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.vm;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.Instruction.BackCommitInstruction;
import org.sonar.sslr.internal.vm.Instruction.BacktrackInstruction;
import org.sonar.sslr.internal.vm.Instruction.CallInstruction;
import org.sonar.sslr.internal.vm.Instruction.ChoiceInstruction;
import org.sonar.sslr.internal.vm.Instruction.CommitInstruction;
import org.sonar.sslr.internal.vm.Instruction.CommitVerifyInstruction;
import org.sonar.sslr.internal.vm.Instruction.EndInstruction;
import org.sonar.sslr.internal.vm.Instruction.FailTwiceInstruction;
import org.sonar.sslr.internal.vm.Instruction.JumpInstruction;
import org.sonar.sslr.internal.vm.Instruction.PredicateChoiceInstruction;
import org.sonar.sslr.internal.vm.Instruction.RetInstruction;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class InstructionTest {

  private Machine machine = mock(Machine.class);

  @Test
  public void jump() {
    Instruction instruction = Instruction.jump(42);
    assertThat(instruction).isInstanceOf(JumpInstruction.class);
    assertThat(instruction.toString()).isEqualTo("Jump 42");

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).jump(42);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void call() {
    Matcher matcher = mock(Matcher.class);
    Instruction instruction = Instruction.call(42, matcher);
    assertThat(instruction).isInstanceOf(CallInstruction.class);
    assertThat(instruction.toString()).isEqualTo("Call 42");

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).pushReturn(1, matcher, 42);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void choice() {
    Instruction instruction = Instruction.choice(42);
    assertThat(instruction).isInstanceOf(ChoiceInstruction.class);
    assertThat(instruction.toString()).isEqualTo("Choice 42");

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).pushBacktrack(42);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void predicateChoice() {
    Instruction instruction = Instruction.predicateChoice(42);
    assertThat(instruction).isInstanceOf(PredicateChoiceInstruction.class);
    assertThat(instruction.toString()).isEqualTo("PredicateChoice 42");

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).setIgnoreErrors(true);
    inOrder.verify(machine).pushBacktrack(42);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void commit() {
    Instruction instruction = Instruction.commit(42);
    assertThat(instruction).isInstanceOf(CommitInstruction.class);
    assertThat(instruction.toString()).isEqualTo("Commit " + 42);

    MachineStack stack = new MachineStack(new MachineStack(null));
    when(machine.peek()).thenReturn(stack);
    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine, times(2)).peek();
    inOrder.verify(machine).pop();
    inOrder.verify(machine).jump(42);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void commitVerify() {
    Instruction instruction = Instruction.commitVerify(42);
    assertThat(instruction).isInstanceOf(CommitVerifyInstruction.class);
    assertThat(instruction.toString()).isEqualTo("CommitVerify " + 42);

    MachineStack stack = new MachineStack(new MachineStack(null));
    when(machine.peek()).thenReturn(stack);
    when(machine.getIndex()).thenReturn(13);
    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine, times(3)).peek();
    inOrder.verify(machine).pop();
    inOrder.verify(machine).jump(42);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void ret() {
    Instruction instruction = Instruction.ret();
    assertThat(instruction).isInstanceOf(RetInstruction.class);
    assertThat(instruction.toString()).isEqualTo("Ret");
    assertThat(instruction).as("singleton").isSameAs(Instruction.ret());

    MachineStack stack = mock(MachineStack.class);
    when(stack.getAddress()).thenReturn(42);
    when(stack.isIgnoreErrors()).thenReturn(true);
    when(machine.peek()).thenReturn(stack);
    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).createNode();
    inOrder.verify(machine).peek();
    inOrder.verify(machine).setIgnoreErrors(true);
    inOrder.verify(machine).setAddress(42);
    inOrder.verify(machine).popReturn();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void backtrack() {
    Instruction instruction = Instruction.backtrack();
    assertThat(instruction).isInstanceOf(BacktrackInstruction.class);
    assertThat(instruction.toString()).isEqualTo("Backtrack");
    assertThat(instruction).as("singleton").isSameAs(Instruction.backtrack());

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void end() {
    Instruction instruction = Instruction.end();
    assertThat(instruction).isInstanceOf(EndInstruction.class);
    assertThat(instruction.toString()).isEqualTo("End");
    assertThat(instruction).as("singleton").isSameAs(Instruction.end());

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).setAddress(-1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void failTwice() {
    Instruction instruction = Instruction.failTwice();
    assertThat(instruction).isInstanceOf(FailTwiceInstruction.class);
    assertThat(instruction.toString()).isEqualTo("FailTwice");
    assertThat(instruction).as("singleton").isSameAs(Instruction.failTwice());

    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).pop();
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void backCommit() {
    Instruction instruction = Instruction.backCommit(42);
    assertThat(instruction).isInstanceOf(BackCommitInstruction.class);
    assertThat(instruction.toString()).isEqualTo("BackCommit 42");

    MachineStack stack = mock(MachineStack.class);
    when(stack.getIndex()).thenReturn(13);
    when(stack.isIgnoreErrors()).thenReturn(true);
    when(machine.peek()).thenReturn(stack);
    instruction.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).peek();
    inOrder.verify(machine).setIndex(13);
    inOrder.verify(machine).setIgnoreErrors(true);
    inOrder.verify(machine).pop();
    inOrder.verify(machine).jump(42);
    verifyNoMoreInteractions(machine);
  }

}
