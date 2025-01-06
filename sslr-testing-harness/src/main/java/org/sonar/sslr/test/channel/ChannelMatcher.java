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
package org.sonar.sslr.test.channel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

public class ChannelMatcher<O> extends BaseMatcher<Channel<O>> {

  private final String sourceCode;
  private final O output;
  private final CodeReader reader;

  public ChannelMatcher(String sourceCode, O output) {
    this.sourceCode = sourceCode;
    this.output = output;
    this.reader = new CodeReader(sourceCode);
  }

  public ChannelMatcher(CodeReader reader, O output) {
    this.output = output;
    this.sourceCode = new String(reader.peek(30));
    this.reader = reader;
  }

  @Override
  public boolean matches(Object arg0) {
    if (!(arg0 instanceof Channel)) {
      return false;
    }
    Channel<O> channel = (Channel<O>) arg0;
    return channel.consume(reader, output);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("Channel consumes '" + sourceCode + "'");
  }

}
