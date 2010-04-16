/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.impl.LexerOutput;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BlackHoleChannelTest {

  private LexerOutput output = new LexerOutput();
  private BlackHoleChannel channel = new BlackHoleChannel();

  @Test
  public void testConsumAnything() {
    assertTrue(channel.consum(new CodeReader("$"), output));
    assertTrue(channel.consum(new CodeReader("\n"), output));
    assertTrue(channel.consum(new CodeReader("g"), output));
    assertTrue(channel.consum(new CodeReader("-"), output));
    assertTrue(channel.consum(new CodeReader("1"), output));
  }

  @Test
  public void testConsumNumber() {
    CodeReader reader = new CodeReader("   \t\n\r123");
    assertTrue(channel.consum(reader, output));
    assertThat((char) reader.peek(), is('1'));
  }
}
