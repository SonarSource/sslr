/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
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

public class TokenTypeClassTest {

  private TokenTypeClassExpression expression = new TokenTypeClassExpression(GenericTokenType.class);
  private Machine machine = mock(Machine.class);

  @Test
  public void should_compile() {
    assertThat(expression.compile(new CompilationHandler())).containsOnly(expression);
    assertThat(expression.toString()).isEqualTo("TokenTypeClass " + GenericTokenType.class);
  }

  @Test
  public void should_match() {
    Token token = mock(Token.class);
    when(token.getType()).thenReturn(GenericTokenType.IDENTIFIER);
    when(machine.length()).thenReturn(1);
    when(machine.tokenAt(0)).thenReturn(token);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).createLeafNode(expression, 1);
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
    Token token = mock(Token.class);
    when(token.getType()).thenReturn(mock(TokenType.class));
    when(machine.length()).thenReturn(1);
    when(machine.tokenAt(0)).thenReturn(token);
    expression.execute(machine);
    InOrder inOrder = Mockito.inOrder(machine);
    inOrder.verify(machine).length();
    inOrder.verify(machine).tokenAt(0);
    inOrder.verify(machine).backtrack();
    verifyNoMoreInteractions(machine);
  }

}
