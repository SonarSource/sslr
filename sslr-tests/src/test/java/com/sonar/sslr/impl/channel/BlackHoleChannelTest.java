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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.sonar.sslr.test.channel.ChannelMatchers.*;

import org.sonar.sslr.channel.CodeReader;
import org.junit.Test;
import com.sonar.sslr.impl.Lexer;

public class BlackHoleChannelTest {

  private final Lexer lexer = Lexer.builder().build();
  private final BlackHoleChannel channel = new BlackHoleChannel("[ \\t]+");

  @Test
  public void testConsumeOneCharacter() {
    assertThat(channel, consume(" ", lexer));
    assertThat(channel, consume("\t", lexer));
    assertThat(channel, not(consume("g", lexer)));
    assertThat(channel, not(consume("-", lexer)));
    assertThat(channel, not(consume("1", lexer)));
  }

  @Test
  public void consumeSeveralCharacters() {
    CodeReader reader = new CodeReader("   \t123");
    assertThat(channel, consume(reader, lexer));
    assertThat(reader, hasNextChar('1'));
  }
}
