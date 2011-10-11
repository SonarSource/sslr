/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.lexer.LexerMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.sonar.test.channel.ChannelMatchers.consume;

import org.junit.Before;
import org.junit.Test;

import com.sonar.sslr.api.LexerOutput;

public class CommentChannelTest {

  private CommentRegexpChannel channel;
  private LexerOutput output;

  @Before
  public void init() {
    output = new LexerOutput();
  }

  @Test
  public void testCommentRegexpNoRegression() {
    channel = new CommentRegexpChannel("//.*");
    assertThat(channel, not(consume("This is not a comment", output)));
    assertThat(channel, consume("//My Comment\n second line", output));
    assertThat(output, hasComment("//My Comment"));
    assertThat(output, hasOriginalComment("//My Comment"));
  }
  
  @Test
  public void testCommentSubstring() {
    channel = new CommentRegexpChannel("//.*", 2, 1);
    assertThat(channel, not(consume("This is not a comment", output)));
    assertThat(channel, consume("//My Comment\n second line", output));
    assertThat(output, hasComment("My Commen"));
    assertThat(output, hasOriginalComment("//My Comment"));
  }
  
  @Test
  public void testCommentTrimBeforeRemove() {
    channel = new CommentRegexpChannel("//.*", 2, 1, true);
    assertThat(channel, not(consume("This is not a comment", output)));
    assertThat(channel, consume("//My Comment    \n second line", output));
    assertThat(output, hasComment("My Commen"));
    assertThat(output, hasOriginalComment("//My Comment    "));
  }
  
}
