/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.lexer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.LexerOutput;

class ConsumeMatcher extends BaseMatcher<Channel<LexerOutput>> {

  private final LexerOutput output;
  private final CodeReader reader;

  ConsumeMatcher(CodeReader reader, LexerOutput output) {
    this.reader = reader;
    this.output = output;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Channel)) {
      return false;
    }
    Channel<LexerOutput> channel = (Channel<LexerOutput>) obj;
    return channel.consume(reader, output);
  }

  public void describeTo(Description desc) {
    desc.appendText("Channel consumes '" + reader.toString() + "'");
  }
}
