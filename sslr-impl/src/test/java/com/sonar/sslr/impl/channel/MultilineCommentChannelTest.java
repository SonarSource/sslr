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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MultilineCommentChannelTest {

  private LexerOutput output = new LexerOutput();
  private MultilineCommentChannel channel;

  @Test
  public void testConsumCommentStartingWithOneCharacter() {
    channel = new MultilineCommentChannel("/*", "*/");
    assertTrue(channel.consum(new CodeReader("/*/ my comment \n second line*/   word"), output));
    assertThat(output.getLastToken().getValue(), is("/*/ my comment \n second line*/"));
  }

  @Test
  public void testNotConsumWord() {
    channel = new MultilineCommentChannel("/*", "*/");
    assertFalse(channel.consum(new CodeReader("word"), output));
  }
}
