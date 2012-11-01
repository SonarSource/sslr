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

  public static Object sequence(Object... elements) {
    return convertToSequenceMatcher(elements);
  }

  public static Object firstOf(Object... elements) {
    if (elements.length == 1) {
      return convertToMatcher(elements[0]);
    }
    return new FirstOfMatcher(convertToMatchers(elements));
  }

  public static Object optional(Object... elements) {
    return new OptionalMatcher(convertToSequenceMatcher(elements));
  }

  public static Object oneOrMore(Object... elements) {
    return new OneOrMoreMatcher(convertToSequenceMatcher(elements));
  }

  public static Object zeroOrMore(Object... elements) {
    return new ZeroOrMoreMatcher(convertToSequenceMatcher(elements));
  }

  public static Object next(Object... elements) {
    return new TestMatcher(convertToSequenceMatcher(elements));
  }

  public static Object nextNot(Object... elements) {
    return new TestNotMatcher(convertToSequenceMatcher(elements));
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
    return new TokenMatcher(tokenType, convertToMatcher(element));
  }

  private static Matcher convertToSequenceMatcher(Object... elements) {
    if (elements.length == 1) {
      return convertToMatcher(elements[0]);
    }
    return new SequenceMatcher(convertToMatchers(elements));
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
