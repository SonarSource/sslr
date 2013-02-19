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

import com.google.common.base.Preconditions;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;

/**
 * TODO Replacement for {@link org.sonar.sslr.internal.matchers.MatchersUtils}
 */
public class MatchersUtils2 {

  private MatchersUtils2() {
  }

  public static ParsingExpression convertToSingleMatcher(Object... elements) {
    Preconditions.checkNotNull(elements);

    if (elements.length == 1) {
      return convertToMatcher(elements[0]);
    }
    return new SequenceExpression(convertToMatchers(elements));
  }

  public static ParsingExpression[] convertToMatchers(Object... elements) {
    Preconditions.checkNotNull(elements);
    Preconditions.checkArgument(elements.length > 0);

    // TODO
    // if (elements.length == 1 && elements[0] instanceof SequenceMatcher) {
    // return ((SequenceMatcher) elements[0]).getSubMatchers();
    // }

    ParsingExpression[] matchers = new ParsingExpression[elements.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToMatcher(elements[i]);
    }
    return matchers;
  }

  public static ParsingExpression convertToMatcher(Object e) {
    Preconditions.checkNotNull(e);

    if (e instanceof ParsingExpression) {
      return (ParsingExpression) e;
    } else if (e instanceof String) {
      return new StringExpression((String) e);
    } else if (e instanceof Character) {
      return new StringExpression(((Character) e).toString());
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + e.getClass().toString());
    }
  }

}
