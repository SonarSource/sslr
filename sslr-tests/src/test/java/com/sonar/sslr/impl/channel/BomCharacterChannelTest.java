/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
