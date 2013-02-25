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
    Token token2 = token(GenericTokenType.IDENTIFIER, 2);
    when(machine.tokenAt(0)).thenReturn(token1);
    when(machine.tokenAt(1)).thenReturn(token2);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).tokenAt(1);
    inOrder.verify(machine).createLeafNode(expression, 1);
    inOrder.verify(machine).jump(1);
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_match2() {
    Token token0 = token(GenericTokenType.IDENTIFIER, 1);
    Token token1 = token(GenericTokenType.IDENTIFIER, 1);
    Token token2 = token(GenericTokenType.EOF, 1);
    when(machine.getIndex()).thenReturn(1);
    when(machine.tokenAt(-1)).thenReturn(token0);
    when(machine.tokenAt(0)).thenReturn(token1);
    when(machine.tokenAt(1)).thenReturn(token2);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).getIndex();
    inOrder.verify(machine).tokenAt(-1);
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).tokenAt(1);
    inOrder.verify(machine).createLeafNode(expression, 1);
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
