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

import java.io.IOException;
import java.io.Reader;

/**
 * This class is a special CodeReaderFilter that uses Channels to filter the character stream before it is passed to the main channels
 * declared for the CodeReader.
 *
 */
public final class ChannelCodeReaderFilter<O> extends CodeReaderFilter<O> {

  @SuppressWarnings("unchecked")
  private Channel<O>[] channels = new Channel[0];

  private CodeReader internalCodeReader;

  /**
   * Creates a CodeReaderFilter that will use the provided Channels to filter the character stream it gets from its reader.
   *
   * @param channels
   *          the different channels
   */
  public ChannelCodeReaderFilter(Channel<O>... channels) {
    super();
    this.channels = channels;
  }

  /**
   * Creates a CodeReaderFilter that will use the provided Channels to filter the character stream it gets from its reader. And optionally,
   * it can push token to the provided output object.
   *
   * @param output
   *          the object that may accept tokens
   * @param channels
   *          the different channels
   */
  public ChannelCodeReaderFilter(O output, Channel<O>... channels) {
    super(output);
    this.channels = channels;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setReader(Reader reader) {
    super.setReader(reader);
    internalCodeReader = new CodeReader(reader, getConfiguration());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(char[] filteredBuffer, int offset, int length) throws IOException {
    int currentOffset = offset;
    if (internalCodeReader.peek() == -1) {
      return -1;
    }
    int initialOffset = currentOffset;
    while (currentOffset < filteredBuffer.length) {
      if (internalCodeReader.peek() == -1) {
        break;
      }
      boolean consumed = false;
      for (Channel<O> channel : channels) {
        if (channel.consume(internalCodeReader, getOutput())) {
          consumed = true;
          break;
        }
      }
      if (!consumed) {
        int charRead = internalCodeReader.pop();
        filteredBuffer[currentOffset] = (char) charRead;
        currentOffset++;
      }
    }
    return currentOffset - initialOffset;
  }

}
