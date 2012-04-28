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
package com.sonar.sslr.test.parser;

import org.hamcrest.Matcher;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.Parser;

public final class ParserMatchers {

  private ParserMatchers() {
  }

  public static Matcher<com.sonar.sslr.impl.matcher.Matcher> match(String sourceCode, Lexer lexer) {
    return new MatchMatcher(sourceCode, lexer);
  }

  public static Matcher<Parser> parse(String sourceCode) {
    return new ParseMatcher(sourceCode);
  }

  public static Matcher<Parser> notParse(String sourceCode) {
    return new NotParseMatcher(sourceCode);
  }

}
