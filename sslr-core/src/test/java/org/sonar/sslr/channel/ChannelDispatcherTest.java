/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2021 SonarSource SA
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
package org.sonar.sslr.channel;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.ChannelDispatcher;
import org.sonar.sslr.channel.CodeReader;

import org.junit.Test;

public class ChannelDispatcherTest {

  @Test
  public void shouldRemoveSpacesFromString() {
    ChannelDispatcher<StringBuilder> dispatcher = ChannelDispatcher.builder().addChannel(new SpaceDeletionChannel()).build();
    StringBuilder output = new StringBuilder();
    dispatcher.consume(new CodeReader("two words"), output);
    assertThat(output.toString(), is("twowords"));
  }

  @Test
  public void shouldAddChannels() {
    ChannelDispatcher<StringBuilder> dispatcher = ChannelDispatcher.builder().addChannels(new SpaceDeletionChannel(), new FakeChannel()).build();
    assertThat(dispatcher.getChannels().length, is(2));
    assertThat(dispatcher.getChannels()[0], is(instanceOf(SpaceDeletionChannel.class)));
    assertThat(dispatcher.getChannels()[1], is(instanceOf(FakeChannel.class)));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoChannelToConsumeNextCharacter() {
    ChannelDispatcher<StringBuilder> dispatcher = ChannelDispatcher.builder().failIfNoChannelToConsumeOneCharacter().build();
    dispatcher.consume(new CodeReader("two words"), new StringBuilder());
  }

  private static class SpaceDeletionChannel extends Channel<StringBuilder> {
    @Override
    public boolean consume(CodeReader code, StringBuilder output) {
      if (code.peek() == ' ') {
        code.pop();
      } else {
        output.append((char) code.pop());
      }
      return true;
    }
  }

  private static class FakeChannel extends Channel<StringBuilder> {
    @Override
    public boolean consume(CodeReader code, StringBuilder output) {
      boolean b = true;
      return b;
    }
  }

}
