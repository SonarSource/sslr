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
package org.sonar.sslr.parser;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.internal.matchers.*;

/**
 * Provides methods to define rules for {@link LexerlessGrammar}.
 *
 * @since 2.0
 */
// TODO Godin: Retrofit methods with varargs (SSLR-215)
public final class GrammarOperators {

  private GrammarOperators() {
  }

  public static Object sequence(Object... elements) {
    return MatchersUtils.convertToSingleMatcher(elements);
  }

  public static Object firstOf(Object... elements) {
    Preconditions.checkNotNull(elements);

    if (elements.length == 1) {
      return MatchersUtils.convertToMatcher(elements[0]);
    }
    return new FirstOfMatcher(MatchersUtils.convertToMatchers(elements));
  }

  public static Object optional(Object... elements) {
    return new OptionalMatcher(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object oneOrMore(Object... elements) {
    return new OneOrMoreMatcher(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object zeroOrMore(Object... elements) {
    return new ZeroOrMoreMatcher(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object next(Object... elements) {
    return new TestMatcher(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object nextNot(Object... elements) {
    return new TestNotMatcher(MatchersUtils.convertToSingleMatcher(elements));
  }

  public static Object regexp(String regexp) {
    return new PatternMatcher(regexp);
  }

  public static Object endOfInput() {
    return new EndOfInputMatcher();
  }

  public static Object nothing() {
    return new NothingMatcher();
  }

  public static Object token(TokenType tokenType, Object element) {
    return new TokenMatcher(tokenType, MatchersUtils.convertToMatcher(element));
  }

}
