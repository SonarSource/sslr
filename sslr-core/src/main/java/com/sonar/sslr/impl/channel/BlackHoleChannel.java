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
package com.sonar.sslr.impl.channel;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.sonar.sslr.impl.Lexer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows to skip characters, which match given regular expression.
 * <p>
 * Mostly this channel is used with regular expression "\s++" to remove all whitespace characters.
 * And in such case this channel should be the first one in a sequence of channels for performance reasons,
 * because generally whitespace characters are encountered more often than all other and especially between others.
 * </p>
 */
public class BlackHoleChannel extends Channel<Lexer> {

  private final Matcher matcher;

  /**
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public BlackHoleChannel(String regexp) {
    matcher = Pattern.compile(regexp).matcher("");
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    return code.popTo(matcher, EmptyAppendable.INSTANCE) != -1;
  }

  private static class EmptyAppendable implements Appendable {

    private static final Appendable INSTANCE = new EmptyAppendable();

    @Override
    public Appendable append(CharSequence csq) throws IOException {
      return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
      return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      return this;
    }
  }

}
