/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.Matchers.consume;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.LexerOutput;

public class BlackHoleChannelTest {

  private LexerOutput output = new LexerOutput();
  private BlackHoleChannel channel = new BlackHoleChannel();

  @Test
  public void testConsumAnything() {
    assertThat(channel, consume(new CodeReader("$"), output));
    assertThat(channel, consume(new CodeReader("\n"), output));
    assertThat(channel, consume(new CodeReader("g"), output));
    assertThat(channel, consume(new CodeReader("-"), output));
    assertThat(channel, consume(new CodeReader("1"), output));
  }

  @Test
  public void testConsumNumber() {
    CodeReader reader = new CodeReader("   \t\n\r123");
    assertThat(channel, consume(reader, output));
    assertThat((char) reader.peek(), is('1'));
  }
}
