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

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.sslr.internal.vm.Machine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TokensBridgeExpressionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private TokenType fromType = mock(TokenType.class);
  private TokenType toType = mock(TokenType.class);
  private TokenType anotherType = mock(TokenType.class);
  private TokensBridgeExpression expression = new TokensBridgeExpression(fromType, toType);
  private Machine machine = mock(Machine.class);

  @Test
  public void should_match() {
    when(machine.length()).thenReturn(5);
    Token token1 = token(fromType);
    Token token2 = token(fromType);
    Token token3 = token(anotherType);
    Token token4 = token(toType);
    Token token5 = token(toType);
    when(machine.tokenAt(0)).thenReturn(token1);
    when(machine.tokenAt(1)).thenReturn(token2);
    when(machine.tokenAt(2)).thenReturn(token3);
    when(machine.tokenAt(3)).thenReturn(token4);
    when(machine.tokenAt(4)).thenReturn(token5);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).tokenAt(1);
    inOrder.verify(machine).tokenAt(2);
    inOrder.verify(machine).tokenAt(3);
    inOrder.verify(machine).tokenAt(4);
    inOrder.verify(machine, times(5)).createLeafNode(expression, 1);
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
    when(machine.length()).thenReturn(2);
    Token token1 = token(anotherType);
    when(machine.tokenAt(0)).thenReturn(token1);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  @Test
  public void should_backtrack3() {
    when(machine.length()).thenReturn(2);
    Token token1 = token(fromType);
    Token token2 = token(fromType);
    when(machine.tokenAt(0)).thenReturn(token1);
    when(machine.tokenAt(1)).thenReturn(token2);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).tokenAt(1);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

  private static Token token(TokenType tokenType) {
    Token token = mock(Token.class);
    when(token.getType()).thenReturn(tokenType);
    return token;
  }

}
