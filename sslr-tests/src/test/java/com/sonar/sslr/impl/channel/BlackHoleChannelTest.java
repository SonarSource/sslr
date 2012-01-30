/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.sonar.test.channel.ChannelMatchers.*;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.impl.Lexer;

public class BlackHoleChannelTest {

  private final Lexer lexer = Lexer.builder().build();
  private final BlackHoleChannel channel = new BlackHoleChannel("[ \\t]+");

  @Test
  public void testConsumeOneCharacter() {
    assertThat(channel, consume(" ", lexer));
    assertThat(channel, consume("\t", lexer));
    assertThat(channel, not(consume("g", lexer)));
    assertThat(channel, not(consume("-", lexer)));
    assertThat(channel, not(consume("1", lexer)));
  }

  @Test
  public void consumeSeveralCharacters() {
    CodeReader reader = new CodeReader("   \t123");
    assertThat(channel, consume(reader, lexer));
    assertThat(reader, hasNextChar('1'));
  }
}
