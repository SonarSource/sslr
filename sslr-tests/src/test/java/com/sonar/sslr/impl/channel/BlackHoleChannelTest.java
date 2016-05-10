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
