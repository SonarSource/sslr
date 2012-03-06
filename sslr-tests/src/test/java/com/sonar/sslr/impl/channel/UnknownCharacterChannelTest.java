/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

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
    assertFalse(channel.consume(new CodeReader(""), null));
  }

  @Test
  public void shouldConsumeBomCharacter() {
    assertTrue(channel.consume(new CodeReader("\uFEFF"), lexer));
    assertThat(lexer.getTokens().size(), is(0));
  }

  private void check(String input, Channel<Lexer> channel, TokenType expectedTokenType, String expectedTokenValue, Lexer lexer) {
    CodeReader code = new CodeReader(new StringReader(input));

    assertTrue(channel.consume(code, lexer));
    assertThat(lexer.getTokens().size(), is(1));
    assertThat(lexer.getTokens().get(0).getType(), is(expectedTokenType));
    assertThat(lexer.getTokens().get(0).getValue(), is(expectedTokenValue));
  }

}
