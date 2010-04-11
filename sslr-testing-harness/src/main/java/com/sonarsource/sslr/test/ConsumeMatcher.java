/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonarsource.sslr.impl.LexerOutput;

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
    return channel.consum(reader, output);
  }

  public void describeTo(Description desc) {
    desc.appendText("Channel consumes '" + reader.toString() + "'");
  }
}
