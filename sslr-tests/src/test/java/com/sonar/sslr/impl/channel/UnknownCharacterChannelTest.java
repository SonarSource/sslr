/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.channel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;

public class UnknownCharacterChannelTest {

  private final Lexer lexer = Lexer.builder().build();
  private final UnknownCharacterChannel channel = new UnknownCharacterChannel();

  @Test
  public void shouldConsumeAnyCharacter() {
    check("'", channel, new Token(GenericTokenType.UNKNOWN_CHAR, "'"), lexer.builder().build(), true);
    check("a", channel, new Token(GenericTokenType.UNKNOWN_CHAR, "a"), lexer.builder().build(), true);
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

  public void check(String input, Channel<Lexer> channel, Token expectedOutput, Lexer lexer, boolean assertion) {
    List<Token> expected = Lists.newArrayList(expectedOutput);
    CodeReader code = new CodeReader(new StringReader(input));
    if (assertion) {
      assertTrue(channel.consume(code, lexer));
      assertThat(lexer.getTokens(), is(expected));
    } else {
      assertFalse(channel.consume(code, lexer));
    }
  }
}
