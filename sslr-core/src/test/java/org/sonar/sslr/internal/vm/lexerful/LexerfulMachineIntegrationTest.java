/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.Machine;
import org.sonar.sslr.internal.vm.SequenceExpression;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LexerfulMachineIntegrationTest {

  private Token[] tokens;

  @Test
  public void tokenType() {
    Instruction[] instructions = new TokenTypeExpression(GenericTokenType.IDENTIFIER).compile(new CompilationHandler());
    assertThat(Machine.execute(instructions, token(GenericTokenType.IDENTIFIER))).isTrue();
    assertThat(Machine.execute(instructions, token(GenericTokenType.LITERAL))).isFalse();
  }

  @Test
  public void tokenTypes() {
    Instruction[] instructions = new TokenTypesExpression(GenericTokenType.IDENTIFIER, GenericTokenType.LITERAL).compile(new CompilationHandler());
    tokens = new Token[] {token(GenericTokenType.IDENTIFIER)};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
    tokens = new Token[] {token(GenericTokenType.LITERAL)};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
    tokens = new Token[] {token(GenericTokenType.UNKNOWN_CHAR)};
    assertThat(Machine.execute(instructions, tokens)).isFalse();
  }

  @Test
  public void tokenValue() {
    Instruction[] instructions = new TokenValueExpression("foo").compile(new CompilationHandler());
    assertThat(Machine.execute(instructions, token("foo"))).isTrue();
    assertThat(Machine.execute(instructions, token("bar"))).isFalse();
  }

  @Test
  public void anyToken() {
    Instruction[] instructions = AnyTokenExpression.INSTANCE.compile(new CompilationHandler());
    assertThat(Machine.execute(instructions, token("foo"))).isTrue();
  }

  @Test
  public void tokensBridge() {
    Instruction[] instructions = new TokensBridgeExpression(GenericTokenType.IDENTIFIER, GenericTokenType.LITERAL).compile(new CompilationHandler());
    tokens = new Token[] {token(GenericTokenType.IDENTIFIER), token(GenericTokenType.LITERAL)};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
    tokens = new Token[] {token(GenericTokenType.IDENTIFIER), token(GenericTokenType.IDENTIFIER), token(GenericTokenType.LITERAL)};
    assertThat(Machine.execute(instructions, tokens)).isFalse();
    tokens = new Token[] {token(GenericTokenType.IDENTIFIER), token(GenericTokenType.IDENTIFIER), token(GenericTokenType.LITERAL), token(GenericTokenType.LITERAL)};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
    tokens = new Token[] {token(GenericTokenType.IDENTIFIER), token(GenericTokenType.UNKNOWN_CHAR), token(GenericTokenType.LITERAL)};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
  }

  @Test
  public void tokenTypeClass() {
    Instruction[] instructions = new TokenTypeClassExpression(GenericTokenType.class).compile(new CompilationHandler());
    tokens = new Token[] {token(GenericTokenType.IDENTIFIER)};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
  }

  @Test
  public void adjacent() {
    Instruction[] instructions =
        new SequenceExpression(
            new TokenValueExpression("foo"),
            AdjacentExpression.INSTANCE,
            new TokenValueExpression("bar")).compile(new CompilationHandler());
    tokens = new Token[] {token(1, 1, "foo"), token(1, 4, "bar")};
    assertThat(Machine.execute(instructions, tokens)).isTrue();
    tokens = new Token[] {token(1, 1, "foo"), token(1, 5, "bar")};
    assertThat(Machine.execute(instructions, tokens)).isFalse();
  }

  private static Token token(TokenType type) {
    return when(mock(Token.class).getType()).thenReturn(type).getMock();
  }

  private static Token token(String value) {
    return when(mock(Token.class).getValue()).thenReturn(value).getMock();
  }

  private static Token token(int line, int column, String value) {
    Token token = mock(Token.class);
    when(token.getLine()).thenReturn(line);
    when(token.getColumn()).thenReturn(column);
    when(token.getValue()).thenReturn(value);
    return token;
  }

}
