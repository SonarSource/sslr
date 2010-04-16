/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.channel;

import java.io.IOException;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

import com.sonar.sslr.impl.LexerOutput;

public class BlackHoleChannel implements Channel<LexerOutput> {

  public boolean consum(CodeReader code, LexerOutput output) {
    code.popTo(new BlackHoleEndMatcher(), new EmptyAppendable());
    return true;
  }

  public class BlackHoleEndMatcher implements EndMatcher {

    public boolean match(int toMatch) {
      return toMatch != ' ' && toMatch != '\t' && toMatch != '\r' && toMatch != '\n';
    }

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
