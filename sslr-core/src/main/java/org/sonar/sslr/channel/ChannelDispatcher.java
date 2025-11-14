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
package org.sonar.sslr.channel;

import java.util.ArrayList;
import java.util.List;

public class ChannelDispatcher<O> extends Channel<O> {

  private final boolean failIfNoChannelToConsumeOneCharacter;

  private final Channel<O>[] channels;

  private ChannelDispatcher(Builder builder) {
    this.channels = builder.channels.toArray(new Channel[builder.channels.size()]);
    this.failIfNoChannelToConsumeOneCharacter = builder.failIfNoChannelToConsumeOneCharacter;
  }

  @Override
  public boolean consume(CodeReader code, O output) {
    int nextChar = code.peek();
    while (nextChar != -1) {
      boolean characterConsumed = false;
      for (Channel<O> channel : channels) {
        if (channel.consume(code, output)) {
          characterConsumed = true;
          break;
        }
      }
      if (!characterConsumed) {
        if (failIfNoChannelToConsumeOneCharacter) {
          String message = "None of the channel has been able to handle character '" + (char) code.peek() + "' (decimal value "
            + code.peek() + ") at line " + code.getLinePosition() + ", column " + code.getColumnPosition();
          throw new IllegalStateException(message);
        }
        code.pop();
      }
      nextChar = code.peek();
    }
    return true;
  }

  Channel[] getChannels() {
    return channels;
  }

  /**
   * Get a Builder instance to build a new ChannelDispatcher
   */
  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private final List<Channel> channels = new ArrayList<>();
    private boolean failIfNoChannelToConsumeOneCharacter = false;

    private Builder() {
    }

    public Builder addChannel(Channel channel) {
      channels.add(channel);
      return this;
    }

    public Builder addChannels(Channel... c) {
      for (Channel channel : c) {
        addChannel(channel);
      }
      return this;
    }

    /**
     * If this option is activated, an IllegalStateException will be thrown as soon as a character won't be consumed by any channel.
     */
    public Builder failIfNoChannelToConsumeOneCharacter() {
      failIfNoChannelToConsumeOneCharacter = true;
      return this;
    }

    public <O> ChannelDispatcher<O> build() {
      return new ChannelDispatcher<>(this);
    }

  }
}
