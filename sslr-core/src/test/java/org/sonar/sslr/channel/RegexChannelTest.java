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
package org.sonar.sslr.channel;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.ChannelDispatcher;
import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.CodeReaderConfiguration;
import org.sonar.sslr.channel.RegexChannel;

import org.junit.Test;

public class RegexChannelTest {

  @Test
  public void shouldMatch() {
    ChannelDispatcher<StringBuilder> dispatcher = ChannelDispatcher.builder().addChannel(new MyWordChannel()).addChannel(new BlackholeChannel()).build();
    StringBuilder output = new StringBuilder();
    dispatcher.consume(new CodeReader("my word"), output);
    assertThat(output.toString(), is("<w>my</w> <w>word</w>"));
  }
  
  @Test
  public void shouldMatchTokenLongerThanBuffer() {
    ChannelDispatcher<StringBuilder> dispatcher = ChannelDispatcher.builder().addChannel(new MyLiteralChannel()).build();
    StringBuilder output = new StringBuilder();
    
    CodeReaderConfiguration codeReaderConfiguration = new CodeReaderConfiguration();

    int literalLength = 100000;
    String veryLongLiteral = String.format(String.format("%%0%dd", literalLength), 0).replace("0", "a");
    
    assertThat(veryLongLiteral.length(), is(100000));
    dispatcher.consume(new CodeReader("\">" + veryLongLiteral + "<\"", codeReaderConfiguration), output);
    assertThat(output.toString(), is("<literal>\">" + veryLongLiteral + "<\"</literal>"));
  }

  private static class MyLiteralChannel extends RegexChannel<StringBuilder> {

    public MyLiteralChannel() {
      super("\"[^\"]*+\"");
    }

    @Override
    protected void consume(CharSequence token, StringBuilder output) {
      output.append("<literal>" + token + "</literal>");
    }
  }
  
  private static class MyWordChannel extends RegexChannel<StringBuilder> {

    public MyWordChannel() {
      super("\\w++");
    }

    @Override
    protected void consume(CharSequence token, StringBuilder output) {
      output.append("<w>" + token + "</w>");
    }
  }

  private static class BlackholeChannel extends Channel<StringBuilder> {

    @Override
    public boolean consume(CodeReader code, StringBuilder output) {
      output.append((char) code.pop());
      return true;
    }
  }

}
