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

/**
 * A {@link Matcher} that repeatedly tries its submatcher against the input.
 * Succeeds if its submatcher succeeds at least once.
 */
public class OneOrMoreMatcher implements Matcher {

  private final Matcher subMatcher;

  public OneOrMoreMatcher(Matcher subMatcher) {
    this.subMatcher = subMatcher;
  }

  public boolean match(MatcherContext context) {
    if (!context.getSubContext(subMatcher).runMatcher()) {
      return false;
    }
    int previousIndex = context.getCurrentIndex();
    while (context.getSubContext(subMatcher).runMatcher()) {
      int currentIndex = context.getCurrentIndex();
      if (currentIndex == previousIndex) {
        throw new GrammarException("The inner part of OneOrMore must not allow empty matches");
      }
      previousIndex = currentIndex;
    }
    context.skipNode();
    return true;
  }

}
