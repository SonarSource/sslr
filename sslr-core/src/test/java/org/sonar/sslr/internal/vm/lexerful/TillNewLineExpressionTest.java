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
package org.sonar.sslr.internal.vm.lexerful;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Machine;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TillNewLineExpressionTest {

  private TillNewLineExpression expression = TillNewLineExpression.INSTANCE;
  private Machine machine = mock(Machine.class);

  @Test
  public void should_compile() {
    assertThat(expression.compile(new CompilationHandler())).containsOnly(expression);
    assertThat(expression.toString()).isEqualTo("TillNewLine");
  }

  @Test
  public void should_match() {
    Token token1 = token(GenericTokenType.IDENTIFIER, 1);
    Token token2 = token(GenericTokenType.IDENTIFIER, 1);
    Token token3 = token(GenericTokenType.IDENTIFIER, 2);
    when(machine.tokenAt(0)).thenReturn(token1);
    when(machine.tokenAt(1)).thenReturn(token2);
    when(machine.tokenAt(2)).thenReturn(token3);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).tokenAt(1);
    inOrder.verify(machine).tokenAt(2);
    // Number of created nodes must be equal to the number of consumed tokens (2):
    inOrder.verify(machine, times(2)).createLeafNode(expression, 1);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_match2() {
    Token token0 = token(GenericTokenType.IDENTIFIER, 1);
    Token token1 = token(GenericTokenType.IDENTIFIER, 1);
    Token token2 = token(GenericTokenType.IDENTIFIER, 1);
    Token token3 = token(GenericTokenType.EOF, 1);
    when(machine.getIndex()).thenReturn(1);
    when(machine.tokenAt(-1)).thenReturn(token0);
    when(machine.tokenAt(0)).thenReturn(token1);
    when(machine.tokenAt(1)).thenReturn(token2);
    when(machine.tokenAt(2)).thenReturn(token3);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(-1);
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).tokenAt(1);
    inOrder.verify(machine).tokenAt(2);
    // Number of created nodes must be equal to the number of consumed tokens (2):
    inOrder.verify(machine, times(2)).createLeafNode(expression, 1);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  private static Token token(TokenType tokenType, int line) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(line);
    when(token.getType()).thenReturn(tokenType);
    return token;
  }

}
