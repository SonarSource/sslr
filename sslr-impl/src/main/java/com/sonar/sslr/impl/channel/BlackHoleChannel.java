/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.api.LexerOutput;

public class BlackHoleChannel implements Channel<LexerOutput> {

  private final Matcher matcher;

  public BlackHoleChannel(String regexp) {
    matcher = Pattern.compile(regexp).matcher("");
  }

  public boolean consum(CodeReader code, LexerOutput output) {
    return code.popTo(matcher, new EmptyAppendable()) != -1;
  }

  private static class EmptyAppendable implements Appendable {

    public Appendable append(CharSequence csq) throws IOException {
      return this;
    }

    public Appendable append(char c) throws IOException {
      return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      return this;
    }
  }
}
