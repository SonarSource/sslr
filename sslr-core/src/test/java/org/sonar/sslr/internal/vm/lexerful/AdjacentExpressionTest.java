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
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.Token;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Machine;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AdjacentExpressionTest {

  private AdjacentExpression expression = AdjacentExpression.INSTANCE;
  private Machine machine = mock(Machine.class);

  @Test
  public void should_compile() {
    assertThat(expression.compile(new CompilationHandler())).containsOnly(expression);
    assertThat(expression.toString()).isEqualTo("Adjacent");
  }

  @Test
  public void should_match() {
    Token previousToken = mock(Token.class);
    when(previousToken.getValue()).thenReturn("foo");
    when(previousToken.getLine()).thenReturn(42);
    when(previousToken.getColumn()).thenReturn(13);
    Token nextToken = mock(Token.class);
    when(nextToken.getLine()).thenReturn(42);
    when(nextToken.getColumn()).thenReturn(13 + 3);
    when(machine.getIndex()).thenReturn(1);
    when(machine.tokenAt(-1)).thenReturn(previousToken);
    when(machine.tokenAt(0)).thenReturn(nextToken);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(-1);
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_backtrack() {
    Token previousToken = mock(Token.class);
    when(previousToken.getValue()).thenReturn("foo");
    when(previousToken.getLine()).thenReturn(42);
    when(previousToken.getColumn()).thenReturn(13);
    Token nextToken = mock(Token.class);
    when(nextToken.getLine()).thenReturn(42 + 1);
    when(nextToken.getColumn()).thenReturn(13 + 3);
    when(machine.getIndex()).thenReturn(1);
    when(machine.tokenAt(-1)).thenReturn(previousToken);
    when(machine.tokenAt(0)).thenReturn(nextToken);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(-1);
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_backtrack2() {
    Token previousToken = mock(Token.class);
    when(previousToken.getValue()).thenReturn("foo");
    when(previousToken.getLine()).thenReturn(42);
    when(previousToken.getColumn()).thenReturn(13);
    Token nextToken = mock(Token.class);
    when(nextToken.getLine()).thenReturn(13);
    when(nextToken.getColumn()).thenReturn(42);
    when(machine.getIndex()).thenReturn(1);
    when(machine.tokenAt(-1)).thenReturn(previousToken);
    when(machine.tokenAt(0)).thenReturn(nextToken);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(-1);
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_backtrack3() {
    when(machine.getIndex()).thenReturn(0);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

}
