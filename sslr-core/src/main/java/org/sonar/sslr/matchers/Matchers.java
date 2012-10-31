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
package org.sonar.sslr.matchers;

import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.internal.matchers.*;

// TODO Godin: Retrofit methods with varargs (SSLR-215)
public final class Matchers {

  private Matchers() {
  }

  public static Matcher sequence(Object... elements) {
    if (elements.length == 1) {
      return convertToMatcher(elements[0]);
    }
    return new SequenceMatcher(convertToMatchers(elements));
  }

  public static Matcher firstOf(Object... elements) {
    if (elements.length == 1) {
      return convertToMatcher(elements[0]);
    }
    return new FirstOfMatcher(convertToMatchers(elements));
  }

  public static Matcher optional(Object... elements) {
    return new OptionalMatcher(sequence(elements));
  }

  public static Matcher oneOrMore(Object... elements) {
    return new OneOrMoreMatcher(sequence(elements));
  }

  public static Matcher zeroOrMore(Object... elements) {
    return new ZeroOrMoreMatcher(sequence(elements));
  }

  public static Matcher next(Object... elements) {
    return new TestMatcher(sequence(elements));
  }

  public static Matcher nextNot(Object... elements) {
    return new TestNotMatcher(sequence(elements));
  }

  public static Matcher regexp(String regexp) {
    return new PatternMatcher(regexp);
  }

  public static Matcher endOfInput() {
    return new EndOfInputMatcher();
  }

  public static Matcher nothing() {
    return new NothingMatcher();
  }

  public static Matcher token(TokenType tokenType, Object element) {
    return new GrammarElementMatcher(tokenType.getName())
        .setTokenType(tokenType)
        .is(convertToMatcher(element));
  }

  private static Matcher[] convertToMatchers(Object... elements) {
    Matcher[] matchers = new Matcher[elements.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToMatcher(elements[i]);
    }
    return matchers;
  }

  private static Matcher convertToMatcher(Object element) {
    if (element instanceof Matcher) {
      return (Matcher) element;
    } else if (element instanceof String) {
      return new StringMatcher((String) element);
    } else if (element instanceof Character) {
      return new StringMatcher(Character.toString((Character) element));
    } else {
      // TODO Godin: improve message
      throw new IllegalArgumentException(element.getClass().getName());
    }
  }

}
