/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test;

import org.hamcrest.Matcher;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.LexerOutput;

public class Matchers {

  public final static Matcher<LexerOutput> hasLastToken(String tokenValue, TokenType tokenType) {
    return new HasLastTokenMatcher(tokenValue, tokenType);
  }

  public final static Matcher<Channel<LexerOutput>> consume(String source, LexerOutput output) {
    return new ConsumeMatcher(new CodeReader(source), output);
  }

  public final static Matcher<Channel<LexerOutput>> consume(CodeReader reader, LexerOutput output) {
    return new ConsumeMatcher(reader, output);
  }

  public final static Matcher<Channel<LexerOutput>> notConsume(String source, LexerOutput output) {
    return new NotConsumeMatcher(source, output);
  }
}
