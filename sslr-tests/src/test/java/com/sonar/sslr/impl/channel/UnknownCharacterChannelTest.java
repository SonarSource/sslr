/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
