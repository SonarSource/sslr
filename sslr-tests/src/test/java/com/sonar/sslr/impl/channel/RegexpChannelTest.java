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

import static com.sonar.sslr.test.lexer.LexerMatchers.*;
import static com.sonar.sslr.test.lexer.MockHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.sonar.sslr.test.channel.ChannelMatchers.*;

import org.sonar.sslr.channel.CodeReader;
import org.junit.Test;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;

public class RegexpChannelTest {

  private RegexpChannel channel;
  private final Lexer lexer = mockLexer();

  @Test
  public void testRegexpToHandleNumber() {
    channel = new RegexpChannel(GenericTokenType.CONSTANT, "[0-9]*");
    assertThat(channel, not(consume("Not a number", lexer)));
    assertThat(channel, consume(new CodeReader("56;"), lexer));
    assertThat(lexer.getTokens(), hasToken("56", GenericTokenType.CONSTANT));
  }

  @Test
  public void testColumnNumber() {
    channel = new RegexpChannel(GenericTokenType.CONSTANT, "[0-9]*");
    assertThat(channel, consume("56;", lexer));
    assertThat(lexer.getTokens().get(0).getColumn(), is(0));
  }
}
