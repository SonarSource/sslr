/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.impl.LexerOutput;

class NotConsumeMatcher extends BaseMatcher<Channel<LexerOutput>> {

  private final String sourceCode;
  private final LexerOutput output;

  NotConsumeMatcher(String source, LexerOutput output) {
    this.sourceCode = source;
    this.output = output;
  }

  public boolean matches(Object obj) {
    if ( !(obj instanceof Channel)) {
      return false;
    }
    Channel<LexerOutput> channel = (Channel<LexerOutput>) obj;
    return !channel.consum(new CodeReader(sourceCode), output);
  }

  public void describeTo(Description desc) {
    desc.appendText("Channel should not consum '" + sourceCode + "'");
  }
}
