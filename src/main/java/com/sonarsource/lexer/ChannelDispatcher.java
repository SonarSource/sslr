/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelDispatcher {

  private static final Logger logger = LoggerFactory.getLogger(ChannelDispatcher.class);

  private final Channel[] channels;

  public ChannelDispatcher(List<Channel> tokenizers) {
    this.channels = tokenizers.toArray(new Channel[0]);
  }

  public void read(CodeReader code) {
    int nextChar = code.peek();
    while (nextChar != -1) {
      boolean channelConsumed = false;
      for (Channel channel : channels) {
        if (channel.read(code)) {
          channelConsumed = true;
          break;
        }
      }
      if (!channelConsumed) {
        logger.debug("None of the channel is able to handle character '" + code.peek() + "' at line " + code.getLinePosition()
            + ", column " + code.getColumnPosition());
        code.read();
      }
      nextChar = code.peek();
    }
  }
}