/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
