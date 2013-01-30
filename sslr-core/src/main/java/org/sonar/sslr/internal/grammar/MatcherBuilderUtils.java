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
package org.sonar.sslr.internal.grammar;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.AndMatcher;
import com.sonar.sslr.impl.matcher.TokenTypeClassMatcher;
import com.sonar.sslr.impl.matcher.TokenTypeMatcher;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;
import org.sonar.sslr.grammar.Grammar;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.SequenceMatcher;
import org.sonar.sslr.internal.matchers.StringMatcher;

import java.util.List;

public final class MatcherBuilderUtils {

  private MatcherBuilderUtils() {
  }

  public static MatcherBuilder lexerlessToSingleMatcherBuilder(List<Object> elements) {
    if (elements.size() == 1) {
      return MatcherBuilderUtils.lexerlessToMatcherBuilder(elements.get(0));
    }
    return new ReflexiveMatcherBuilder(SequenceMatcher.class, MatcherBuilderUtils.lexerlessToMatcherBuilders(elements));
  }

  public static MatcherBuilder[] lexerlessToMatcherBuilders(List<Object> elements) {
    MatcherBuilder[] matcherBuilders = new MatcherBuilder[elements.size()];
    for (int i = 0; i < matcherBuilders.length; i++) {
      matcherBuilders[i] = lexerlessToMatcherBuilder(elements.get(i));
    }
    return matcherBuilders;
  }

  public static MatcherBuilder lexerlessToMatcherBuilder(Object element) {
    Preconditions.checkNotNull(element, "Incorrect parsing expression: null");

    if (element instanceof MatcherBuilder) {
      return (MatcherBuilder) element;
    } else if (element instanceof GrammarRuleKey) {
      return new RuleMatcherBuilder((GrammarRuleKey) element);
    } else if (element instanceof String) {
      return new ReflexiveMatcherBuilder(StringMatcher.class, new Object[] {element});
    } else if (element instanceof Character) {
      return new ReflexiveMatcherBuilder(StringMatcher.class, new Object[] {Character.toString((Character) element)});
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + element.getClass().getName());
    }
  }

  public static MatcherBuilder lexerfulToSingleMatcherBuilder(List<Object> elements) {
    if (elements.size() == 1) {
      return MatcherBuilderUtils.lexerfulToMatcherBuilder(elements.get(0));
    }
    return new ReflexiveMatcherBuilder(AndMatcher.class, MatcherBuilderUtils.lexerfulToMatcherBuilders(elements));
  }

  public static MatcherBuilder[] lexerfulToMatcherBuilders(List<Object> elements) {
    MatcherBuilder[] matcherBuilders = new MatcherBuilder[elements.size()];
    for (int i = 0; i < matcherBuilders.length; i++) {
      matcherBuilders[i] = lexerfulToMatcherBuilder(elements.get(i));
    }
    return matcherBuilders;
  }

  public static MatcherBuilder lexerfulToMatcherBuilder(Object element) {
    Preconditions.checkNotNull(element, "Incorrect parsing expression: null");

    if (element instanceof MatcherBuilder) {
      return (MatcherBuilder) element;
    } else if (element instanceof GrammarRuleKey) {
      return new RuleMatcherBuilder((GrammarRuleKey) element);
    } else if (element instanceof String) {
      return new ReflexiveMatcherBuilder(TokenValueMatcher.class, new Object[] {element, false});
    } else if (element instanceof TokenType) {
      TokenType tokenType = (TokenType) element;
      return new ReflexiveMatcherBuilder(TokenTypeMatcher.class, new Object[] {tokenType, tokenType.hasToBeSkippedFromAst(null)});
    } else if (element instanceof Class) {
      return new ReflexiveMatcherBuilder(TokenTypeClassMatcher.class, new Object[] {element});
    } else {
      throw new IllegalArgumentException("Incorrect type of parsing expression: " + element.getClass().getName());
    }
  }

  public static Object[] build(Grammar g, MatcherBuilder[] elements) {
    Object[] matchers = new Object[elements.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = elements[i].build(g);
    }
    return matchers;
  }

}
