/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.vm;

import org.junit.Test;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.ParseNode;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MachineTest {

  @Test
  public void subSequence_not_supported() {
    Machine machine = new Machine("", new Instruction[0]);
    assertThrows(UnsupportedOperationException.class,
      () -> machine.subSequence(0, 0));
  }

  @Test
  public void test_initial_state() {
    Machine machine = new Machine("", new Instruction[2]);
    assertThat(machine.getAddress()).isEqualTo(0);
    assertThat(machine.getIndex()).isEqualTo(0);
    assertThat(machine.peek().isEmpty()).isTrue();
  }

  @Test
  public void should_jump() {
    Machine machine = new Machine("", new Instruction[2]);
    assertThat(machine.getAddress()).isEqualTo(0);
    machine.jump(42);
    assertThat(machine.getAddress()).isEqualTo(42);
    machine.jump(13);
    assertThat(machine.getAddress()).isEqualTo(42 + 13);
  }

  @Test
  public void should_advanceIndex() {
    Machine machine = new Machine("foo bar", new Instruction[2]);
    assertThat(machine.getIndex()).isEqualTo(0);
    assertThat(machine.length()).isEqualTo(7);
    assertThat(machine.charAt(0)).isEqualTo('f');
    assertThat(machine.charAt(1)).isEqualTo('o');
    machine.advanceIndex(3);
    assertThat(machine.getIndex()).isEqualTo(3);
    assertThat(machine.length()).isEqualTo(4);
    assertThat(machine.charAt(0)).isEqualTo(' ');
    assertThat(machine.charAt(1)).isEqualTo('b');
    machine.advanceIndex(1);
    assertThat(machine.getIndex()).isEqualTo(4);
    assertThat(machine.length()).isEqualTo(3);
    assertThat(machine.charAt(0)).isEqualTo('b');
    assertThat(machine.charAt(1)).isEqualTo('a');
  }

  @Test
  public void should_pushReturn() {
    Machine machine = new Machine("foo", new Instruction[3]);
    Matcher matcher = mock(Matcher.class);
    machine.advanceIndex(1);
    machine.jump(1);
    MachineStack previousStack = machine.peek();
    machine.pushReturn(2, matcher, 1);
    assertThat(machine.getAddress()).as("new address").isEqualTo(2);
    assertThat(machine.peek()).isNotSameAs(previousStack);
    assertThat(machine.peek().parent()).isSameAs(previousStack);
    assertThat(machine.peek().index()).as("current index").isEqualTo(1);
    assertThat(machine.peek().address()).as("return address").isEqualTo(1 + 2);
    assertThat(machine.peek().matcher()).isSameAs(matcher);
  }

  @Test
  public void should_detect_left_recursion() {
    Machine machine = new Machine("foo", new Instruction[2]);
    Matcher matcher = mock(Matcher.class);

    machine.advanceIndex(1);
    machine.pushReturn(0, matcher, 1);
    assertThat(machine.peek().calledAddress()).isEqualTo(1);
    assertThat(machine.peek().leftRecursion()).isEqualTo(-1);

    // same rule, but another index of input sequence
    machine.advanceIndex(1);
    machine.pushReturn(0, matcher, 0);
    assertThat(machine.peek().calledAddress()).isEqualTo(1);
    assertThat(machine.peek().leftRecursion()).isEqualTo(1);

    // same rule and index of input sequence
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> machine.pushReturn(0, matcher, 0));
    assertEquals("Left recursion has been detected, involved rule: " + matcher.toString(), thrown.getMessage());
  }

  @Test
  public void should_pushBacktrack() {
    Machine machine = new Machine("foo", new Instruction[2]);
    machine.advanceIndex(1);
    machine.jump(42);
    MachineStack previousStack = machine.peek();
    machine.pushBacktrack(13);
    assertThat(machine.peek()).isNotSameAs(previousStack);
    assertThat(machine.peek().parent()).isSameAs(previousStack);
    assertThat(machine.peek().index()).as("current index").isEqualTo(1);
    assertThat(machine.peek().address()).as("backtrack address").isEqualTo(42 + 13);
    assertThat(machine.peek().matcher()).isNull();
  }

  @Test
  public void should_pop() {
    Machine machine = new Machine("", new Instruction[2]);
    MachineStack previousStack = machine.peek();
    machine.pushBacktrack(13);
    assertThat(machine.peek()).isNotSameAs(previousStack);
    machine.pop();
    assertThat(machine.peek()).isSameAs(previousStack);
  }

  @Test
  public void should_fail() {
    Machine machine = new Machine("", new Instruction[3]);
    Matcher matcher = mock(Matcher.class);
    machine.pushReturn(13, matcher, 0);
    machine.pushReturn(13, matcher, 1);
    machine.backtrack();
    assertThat(machine.getAddress()).isEqualTo(-1);
    // TODO matched=false
  }

  @Test
  public void should_backtrack() {
    Machine machine = new Machine("", new Instruction[4]);
    Matcher matcher = mock(Matcher.class);
    MachineStack previousStack = machine.peek();
    machine.pushBacktrack(42);
    machine.pushReturn(13, matcher, 0);
    machine.pushReturn(13, matcher, 1);
    machine.backtrack();
    assertThat(machine.peek()).isSameAs(previousStack);
    assertThat(machine.getAddress()).isEqualTo(42);
  }

  @Test
  public void should_createLeafNode() {
    Machine machine = new Machine("", new Instruction[2]);
    Matcher matcher = mock(Matcher.class);
    machine.advanceIndex(42);
    machine.createLeafNode(matcher, 13);
    ParseNode node = machine.peek().subNodes().get(0);
    assertThat(node.getMatcher()).isSameAs(matcher);
    assertThat(node.getStartIndex()).isEqualTo(42);
    assertThat(node.getEndIndex()).isEqualTo(42 + 13);
    assertThat(node.getChildren()).isEmpty();
  }

  @Test
  public void should_createNode() {
    Machine machine = new Machine(" ", new Instruction[2]);
    Matcher matcher = mock(Matcher.class);
    machine.advanceIndex(1);
    // remember startIndex and matcher
    machine.pushReturn(0, matcher, 0);
    Matcher subMatcher = mock(Matcher.class);
    machine.createLeafNode(subMatcher, 2);
    machine.createLeafNode(subMatcher, 3);
    machine.createNode();
    ParseNode node = machine.peek().parent().subNodes().get(0);
    assertThat(node.getMatcher()).isSameAs(matcher);
    assertThat(node.getStartIndex()).isEqualTo(1);
    assertThat(node.getEndIndex()).isEqualTo(1 + 2 + 3);
    assertThat(node.getChildren()).hasSize(2);
  }

  @Test
  public void should_use_memo() {
    Machine machine = new Machine("foo", new Instruction[3]);
    MemoParsingExpression matcher = mock(MemoParsingExpression.class);
    when(matcher.shouldMemoize()).thenReturn(true);
    machine.pushBacktrack(0);
    machine.pushReturn(1, matcher, 2);
    machine.advanceIndex(3);
    machine.createNode();
    ParseNode memo = machine.peek().parent().subNodes().get(0);
    machine.backtrack();
    machine.pushReturn(2, matcher, 1);
    assertThat(machine.getAddress()).isEqualTo(2);
    assertThat(machine.getIndex()).isEqualTo(3);
    assertThat(machine.peek().subNodes()).containsOnly(memo);
  }

  @Test
  public void should_not_memorize() {
    Machine machine = new Machine("foo", new Instruction[3]);
    MemoParsingExpression matcher = mock(MemoParsingExpression.class);
    when(matcher.shouldMemoize()).thenReturn(false);
    machine.pushBacktrack(0);
    machine.pushReturn(1, matcher, 2);
    machine.advanceIndex(3);
    machine.createNode();
    machine.backtrack();
    machine.pushReturn(2, matcher, 1);
    assertThat(machine.getAddress()).isEqualTo(1);
    assertThat(machine.getIndex()).isEqualTo(0);
    assertThat(machine.peek().subNodes()).isEmpty();
  }

  @Test
  public void should_not_use_memo() {
    Machine machine = new Machine("foo", new Instruction[3]);
    MemoParsingExpression matcher = mock(MemoParsingExpression.class);
    when(matcher.shouldMemoize()).thenReturn(true);
    machine.pushBacktrack(0);
    machine.pushReturn(2, matcher, 1);
    machine.advanceIndex(3);
    machine.createNode();
    machine.backtrack();
    Matcher anotherMatcher = mock(Matcher.class);
    machine.pushReturn(2, anotherMatcher, 1);
    assertThat(machine.getAddress()).isEqualTo(1);
    assertThat(machine.getIndex()).isEqualTo(0);
    assertThat(machine.peek().subNodes()).isEmpty();
  }

}
