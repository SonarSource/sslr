/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.sonar.test.channel.ChannelMatchers.consume;
import static org.sonar.test.channel.ChannelMatchers.hasNextChar;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.LexerOutput;

public class BlackHoleChannelTest {

  private LexerOutput output = new LexerOutput();
  private BlackHoleChannel channel = new BlackHoleChannel("[ \\t]+");

  @Test
  public void testConsumeOneCharacter() {
    assertThat(channel, consume(" ", output));
    assertThat(channel, consume("\t", output));
    assertThat(channel, not(consume("g", output)));
    assertThat(channel, not(consume("-", output)));
    assertThat(channel, not(consume("1", output)));
  }

  @Test
  public void consumeSeveralCharacters() {
    CodeReader reader = new CodeReader("   \t123");
    assertThat(channel, consume(reader, output));
    assertThat(reader, hasNextChar('1'));
  }
}
