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
  public void testCommentRegexp() {
    channel = new CommentRegexpChannel("//.*");
    assertThat(channel, not(consume("This is not a comment", output)));
    assertThat(channel, consume("//My Comment\n second line", output));
    assertThat(output, hasComment("//My Comment"));
    assertThat(output, hasOriginalComment("//My Comment"));
  }

}
