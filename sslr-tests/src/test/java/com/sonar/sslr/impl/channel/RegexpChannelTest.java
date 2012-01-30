/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.sonar.test.channel.ChannelMatchers.*;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;

public class RegexpChannelTest {

  private RegexpChannel channel;
  private final Lexer lexer = Lexer.builder().build();

  @Test
  public void testRegexpToHandleNumber() {
    channel = new RegexpChannel(GenericTokenType.CONSTANT, "[0-9]*");
    assertThat(channel, not(consume("Not a number", lexer)));
    assertThat(channel, consume(new CodeReader("56;"), lexer));
    assertThat(lexer.getTokens(), hasToken("56", GenericTokenType.CONSTANT));
  }

  @Test
  public void testColumnNumber() {
    channel = new RegexpChannel(GenericTokenType.CONSTANT, "[0-9]*");
    assertThat(channel, consume("56;", lexer));
    assertThat(lexer.getTokens().get(0).getColumn(), is(0));
  }
}
