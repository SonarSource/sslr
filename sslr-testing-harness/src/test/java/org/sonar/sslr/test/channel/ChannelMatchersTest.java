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
package org.sonar.sslr.test.channel;

import org.junit.Test;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.sonar.sslr.test.channel.ChannelMatchers.consume;
import static org.sonar.sslr.test.channel.ChannelMatchers.hasNextChar;

public class ChannelMatchersTest {

  @Test
  public void testConsumeMatcher() {
    Channel<StringBuilder> numberChannel = new Channel<StringBuilder>() {

      @Override
      public boolean consume(CodeReader code, StringBuilder output) {
        if (Character.isDigit(code.peek())) {
          output.append((char) code.pop());
          return true;
        }
        return false;
      }
    };
    StringBuilder output = new StringBuilder();
    assertThat(numberChannel, consume("3", output));
    assertThat(output.toString(), is("3"));
    assertThat(numberChannel, consume(new CodeReader("333333"), output));

    output = new StringBuilder();
    assertThat(numberChannel, not(consume("n", output)));
    assertThat(output.toString(), is(""));
    assertThat(numberChannel, not(consume(new CodeReader("n"), output)));
  }

  @Test
  public void testHasNextChar() {
    assertThat(new CodeReader("123"), hasNextChar('1'));
    assertThat(new CodeReader("123"), not(hasNextChar('n')));
  }
}
