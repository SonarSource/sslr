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
package org.sonar.sslr.impl.grammar;

import com.google.common.base.Preconditions;
import org.sonar.sslr.grammar.Grammar;
import org.sonar.sslr.grammar.GrammarRule;
import org.sonar.sslr.internal.matchers.Matcher;

public class MatcherBuilderUtils {

  public static MatcherBuilder convertToSingleMatcherBuilder(Object[] elements) {
    if (elements.length == 1) {
      return MatcherBuilderUtils.convertToMatcherBuilder(elements[0]);
    }

    return new SequenceMatcherBuilder(MatcherBuilderUtils.convertToMatcherBuilders(elements));
  }

  public static MatcherBuilder[] convertToMatcherBuilders(Object[] elements) {
    MatcherBuilder matcherBuilders[] = new MatcherBuilder[elements.length];
    for (int i = 0; i < matcherBuilders.length; i++) {
      matcherBuilders[i] = convertToMatcherBuilder(elements[i]);
    }
    return matcherBuilders;
  }

  public static MatcherBuilder convertToMatcherBuilder(Object element) {
    Preconditions.checkNotNull(element, "Incorrect parsing expression: null");

    if (element instanceof MatcherBuilder) {
      return (MatcherBuilder) element;
    } else if (element instanceof GrammarRule) {
      return new RuleMatcherBuilder((GrammarRule) element);
    } else if (element instanceof String) {
      return new StringMatcherBuilder((String) element);
    } else if (element instanceof Character) {
      return new StringMatcherBuilder(Character.toString((Character) element));
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + element.getClass().getName());
    }
  }

  public static Matcher[] convertToMatchers(Grammar g, MatcherBuilder... elements) {
    Matcher[] matchers = new Matcher[elements.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = elements[i].build(g);
    }
    return matchers;
  }

}
