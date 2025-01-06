/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.channel;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.test.lexer.LexerMatchers.*;
import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.sonar.sslr.test.channel.ChannelMatchers.*;

import org.junit.Test;
import com.sonar.sslr.impl.Lexer;

public class CommentChannelTest {

  private CommentRegexpChannel channel;
  private final Lexer lexer = mockLexer();

  @Test
  public void testCommentRegexp() {
    channel = new CommentRegexpChannel("//.*");
    assertThat(channel, not(consume("This is not a comment", lexer)));
    assertThat(channel, consume("//My Comment\n second line", lexer));
    lexer.addToken(mockToken(EOF, "EOF"));
    assertThat(lexer.getTokens(), hasComment("//My Comment"));
    assertThat(lexer.getTokens(), hasOriginalComment("//My Comment"));
  }

}
