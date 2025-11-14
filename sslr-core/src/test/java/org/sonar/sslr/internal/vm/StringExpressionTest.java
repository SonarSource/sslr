/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class StringExpressionTest {

  private StringExpression expression = new StringExpression("foo");
  private Machine machine = mock(Machine.class);

  @Test
  public void should_compile() {
    assertThat(expression.compile(new CompilationHandler())).containsOnly(expression);
    assertThat(expression.toString()).isEqualTo("String foo");
  }

  @Test
  public void should_match() {
    when(machine.length()).thenReturn(3);
    when(machine.charAt(0)).thenReturn('f');
    when(machine.charAt(1)).thenReturn('o');
    when(machine.charAt(2)).thenReturn('o');
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).charAt(0);
    inOrder.verify(machine).charAt(1);
    inOrder.verify(machine).charAt(2);
    inOrder.verify(machine).createLeafNode(expression, 3);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_backtrack() {
    when(machine.length()).thenReturn(0);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_backtrack2() {
    when(machine.length()).thenReturn(3);
    when(machine.charAt(0)).thenReturn('b');
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).charAt(0);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

}
