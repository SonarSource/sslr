/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.sonar.test.channel.ChannelMatchers.consume;

import org.junit.Test;

import com.sonar.sslr.api.LexerOutput;

public class InlineCommentChannelTest {

  private LexerOutput output = new LexerOutput();
  private InlineCommentChannel channel;

  @Test
  public void testConsumCommentStartingWithOneCharacter() {
    channel = new InlineCommentChannel("'");
    assertThat(channel, consume("' my comment\n toto", output));
    assertThat(output, hasComment("' my comment"));
  }

  @Test
  public void testConsumCppComment() {
    channel = new InlineCommentChannel("//");
    assertThat(channel, consume("// my comment\r lkjd", output));
    assertThat(output, hasComment("// my comment"));
  }

  @Test
  public void testNotConsumWord() {
    channel = new InlineCommentChannel("'");
    assertThat(channel, not(consume("word", output)));
  }
}
