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
package com.sonar.sslr.impl.channel;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import org.junit.Test;

import java.io.StringReader;

import static com.sonar.sslr.api.GenericTokenType.UNKNOWN_CHAR;
import static com.sonar.sslr.test.lexer.MockHelper.mockLexer;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnknownCharacterChannelTest {

  private final Lexer lexer = mockLexer();
  private final UnknownCharacterChannel channel = new UnknownCharacterChannel();

  @Test
  public void shouldConsumeAnyCharacter() {
    check("'", channel, UNKNOWN_CHAR, "'", mockLexer());
    check("a", channel, UNKNOWN_CHAR, "a", mockLexer());
  }

  @Test
  public void shouldConsumeEofCharacter() {
    assertThat(channel.consume(new CodeReader(""), null)).isFalse();
  }

  private void check(String input, Channel<Lexer> channel, TokenType expectedTokenType, String expectedTokenValue, Lexer lexer) {
    CodeReader code = new CodeReader(new StringReader(input));

    assertThat(channel.consume(code, lexer)).isTrue();
    assertThat(lexer.getTokens().size(), is(1));
    assertThat(lexer.getTokens().get(0).getType(), is(expectedTokenType));
    assertThat(lexer.getTokens().get(0).getValue(), is(expectedTokenValue));
  }

}
