/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
