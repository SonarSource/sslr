/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.channel;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import com.sonar.sslr.impl.Lexer;

public class BlackHoleChannel extends Channel<Lexer> {

  private final Matcher matcher;

  public BlackHoleChannel(String regexp) {
    matcher = Pattern.compile(regexp).matcher("");
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
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
