/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.test.Matchers.consume;
import static com.sonar.sslr.test.Matchers.hasToken;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;

public class RegexpChannelTest {

  private RegexpChannel channel;
  private LexerOutput output;

  @Before
  public void init() {
    output = new LexerOutput();
  }

  @Test
  public void testRegexpToHandleInlineComment() {
    channel = new RegexpChannel(GenericTokenType.COMMENT, "//.*");
    assertThat(channel, not(consume(new CodeReader("This is not a comment"), output)));
    assertThat(channel, consume(new CodeReader("//My Comment\n second line"), output));
    assertThat(output, hasToken("//My Comment", GenericTokenType.COMMENT));
  }

  @Test
  public void testRegexpToHandleNumber() {
    channel = new RegexpChannel(GenericTokenType.CONSTANT, "[0-9]*");
    assertThat(channel, not(consume(new CodeReader("Not a number"), output)));
    assertThat(channel, consume(new CodeReader("56;"), output));
    assertThat(output, hasToken("56", GenericTokenType.CONSTANT));
  }
}
