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
package org.sonar.sslr.channel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The RegexChannel can be used to be called each time the next characters in the character stream match a regular expression
 */
public abstract class RegexChannel<O> extends Channel<O> {

  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;

  /**
   * Create a RegexChannel object with the required regular expression
   *
   * @param regex
   *          regular expression to be used to try matching the next characters in the stream
   */
  public RegexChannel(String regex) {
    matcher = Pattern.compile(regex).matcher("");
  }

  @Override
  public final boolean consume(CodeReader code, O output) {
    if (code.popTo(matcher, tmpBuilder) > 0) {
      consume(tmpBuilder, output);
      tmpBuilder.delete(0, tmpBuilder.length());
      return true;
    }
    return false;
  }

  /**
   * The consume method is called each time the regular expression used to create the RegexChannel object matches the next characters in the
   * character streams.
   *
   * @param token
   *          the token consumed in the character stream and matching the regular expression
   * @param output
   *          the OUTPUT object which can be optionally fed
   */
  protected abstract void consume(CharSequence token, O output);

}
