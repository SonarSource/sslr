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
package org.sonar.sslr.internal.matchers;

public final class MatchersUtils {

  private MatchersUtils() {
  }

  public static Matcher convertToSingleMatcher(Object... elements) {
    if (elements.length == 1) {
      return MatchersUtils.convertToMatcher(elements[0]);
    }
    return new SequenceMatcher(MatchersUtils.convertToMatchers(elements));
  }

  public static Matcher[] convertToMatchers(Object... elements) {
    if (elements.length == 1 && elements[0] instanceof SequenceMatcher) {
      return ((SequenceMatcher) elements[0]).getSubMatchers();
    }

    Matcher[] matchers = new Matcher[elements.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToMatcher(elements[i]);
    }
    return matchers;
  }

  public static Matcher convertToMatcher(Object element) {
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
