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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;

public class CommentChannelTest {

  private CommentRegexpChannel channel;
  private final Lexer lexer = Lexer.builder().build();

  @Test
  public void testCommentRegexp() {
    channel = new CommentRegexpChannel("//.*");
    assertThat(channel, not(consume("This is not a comment", lexer)));
    assertThat(channel, consume("//My Comment\n second line", lexer));
    lexer.addToken(Token.builder(GenericTokenType.EOF, "EOF").build());
    assertThat(lexer.getTokens(), hasComment("//My Comment"));
    assertThat(lexer.getTokens(), hasOriginalComment("//My Comment"));
  }

}
