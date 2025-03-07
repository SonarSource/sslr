/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.vm;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.sslr.grammar.GrammarException;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PatternExpressionTest {

  private PatternExpression expression = new PatternExpression("foo|bar");
  private Machine machine = mock(Machine.class);

  @Test
  public void should_compile() {
    assertThat(expression.compile(new CompilationHandler())).containsOnly(expression);
    assertThat(expression.toString()).isEqualTo("Pattern foo|bar");
  }

  @Test
  public void should_match() {
    when(machine.length()).thenReturn(3);
    when(machine.charAt(0)).thenReturn('f');
    when(machine.charAt(1)).thenReturn('o');
    when(machine.charAt(2)).thenReturn('o');
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine, atLeast(1)).length();
    inOrder.verify(machine, atLeast(1)).charAt(0);
    inOrder.verify(machine, atLeast(1)).charAt(1);
    inOrder.verify(machine, atLeast(1)).charAt(2);
    inOrder.verify(machine).createLeafNode(expression, 3);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);

    // Should reset matcher with empty string:
    try {
      expression.getMatcher().find(1);
      Assert.fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      assertThat(e.getMessage()).isEqualTo("Illegal start index");
    }
  }

  @Test
  public void should_backtrack() {
    when(machine.length()).thenReturn(1);
    when(machine.charAt(0)).thenReturn('z');
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine, atLeast(1)).length();
    inOrder.verify(machine, atLeast(1)).charAt(0);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);

    // Should reset matcher with empty string:
    try {
      expression.getMatcher().find(1);
      Assert.fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      assertThat(e.getMessage()).isEqualTo("Illegal start index");
    }
  }

  @Test
  public void should_catch_StackOverflowError() {
    when(machine.length()).thenReturn(1);
    when(machine.charAt(0)).thenThrow(StackOverflowError.class);
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> expression.execute(machine));
    assertEquals("The regular expression 'foo|bar' has led to a stack overflow error."
      + " This error is certainly due to an inefficient use of alternations. See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5050507",
      thrown.getMessage());
  }

}
