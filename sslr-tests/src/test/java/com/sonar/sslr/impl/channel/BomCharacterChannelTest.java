/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
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

import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.impl.Lexer;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;

public class BomCharacterChannelTest {

  private final Lexer lexer = Lexer.builder().build();
  private final BomCharacterChannel channel = new BomCharacterChannel();

  @Test
  public void shouldConsumeBomCharacter() {
    assertThat(channel.consume(new CodeReader("\uFEFF"), lexer)).isTrue();
    assertThat(lexer.getTokens().size()).isEqualTo(0);
  }

  @Test
  public void shouldNotConsumeOtherCharacters() {
    assertThat(channel.consume(new CodeReader(" "), lexer)).isFalse();
    assertThat(lexer.getTokens().size()).isEqualTo(0);
  }

}
