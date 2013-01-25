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
package org.sonar.sslr.grammar;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.grammar.MatcherBuilderUtils;
import org.sonar.sslr.internal.grammar.ReflexiveMatcherBuilder;
import org.sonar.sslr.internal.matchers.EndOfInputMatcher;
import org.sonar.sslr.internal.matchers.FirstOfMatcher;
import org.sonar.sslr.internal.matchers.NothingMatcher;
import org.sonar.sslr.internal.matchers.OneOrMoreMatcher;
import org.sonar.sslr.internal.matchers.OptionalMatcher;
import org.sonar.sslr.internal.matchers.PatternMatcher;
import org.sonar.sslr.internal.matchers.TestMatcher;
import org.sonar.sslr.internal.matchers.TestNotMatcher;
import org.sonar.sslr.internal.matchers.TokenMatcher;
import org.sonar.sslr.internal.matchers.TriviaMatcher;
import org.sonar.sslr.internal.matchers.ZeroOrMoreMatcher;

import java.util.Collection;
import java.util.Map;

public class GrammarBuilder {

  private final Map<GrammarRule, GrammarRuleDefinition> definitions = Maps.newHashMap();

  public GrammarRuleDefinition rule(GrammarRule rule) {
    GrammarRuleDefinition definition = definitions.get(rule);
    if (definition == null) {
      definition = new GrammarRuleDefinition(rule);
      definitions.put(rule, definition);
    }

    return definition;
  }

  public Collection<GrammarRuleDefinition> rules() {
    return definitions.values();
  }

  public Grammar build() {
    return new Grammar(this);
  }

  public Object sequence(Object e1, Object e2, Object... others) {
    Object[] elements = new Object[2 + others.length];
    elements[0] = e1;
    elements[1] = e2;
    System.arraycopy(others, 0, elements, 2, others.length);
    return MatcherBuilderUtils.convertToSingleMatcherBuilder(elements);
  }

  public Object firstOf(Object e1, Object e2, Object... others) {
    Object[] elements = new Object[2 + others.length];
    elements[0] = e1;
    elements[1] = e2;
    System.arraycopy(others, 0, elements, 2, others.length);
    return new ReflexiveMatcherBuilder(FirstOfMatcher.class, MatcherBuilderUtils.convertToMatcherBuilders(elements));
  }

  public Object optional(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new ReflexiveMatcherBuilder(OptionalMatcher.class, new Object[] {MatcherBuilderUtils.convertToSingleMatcherBuilder(elements)});
  }

  public Object oneOrMore(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new ReflexiveMatcherBuilder(OneOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.convertToSingleMatcherBuilder(elements)});
  }

  public Object zeroOrMore(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new ReflexiveMatcherBuilder(ZeroOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.convertToSingleMatcherBuilder(elements)});
  }

  public Object next(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new ReflexiveMatcherBuilder(TestMatcher.class, new Object[] {MatcherBuilderUtils.convertToSingleMatcherBuilder(elements)});
  }

  public Object nextNot(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new ReflexiveMatcherBuilder(TestNotMatcher.class, new Object[] {MatcherBuilderUtils.convertToSingleMatcherBuilder(elements)});
  }

  public Object regexp(String regexp) {
    return new ReflexiveMatcherBuilder(PatternMatcher.class, new Object[] {regexp});
  }

  public Object endOfInput() {
    return new ReflexiveMatcherBuilder(EndOfInputMatcher.class, new Object[0]);
  }

  public Object nothing(String regexp) {
    return new ReflexiveMatcherBuilder(NothingMatcher.class, new Object[0]);
  }

  public Object token(TokenType tokenType, Object element) {
    return new ReflexiveMatcherBuilder(TokenMatcher.class, new Object[] {tokenType, MatcherBuilderUtils.convertToMatcherBuilder(element)});
  }

  public Object commentTrivia(Object element) {
    return new ReflexiveMatcherBuilder(TriviaMatcher.class, new Object[] {TriviaKind.COMMENT, MatcherBuilderUtils.convertToMatcherBuilder(element)});
  }

  public Object skippedTrivia(Object element) {
    return new ReflexiveMatcherBuilder(TriviaMatcher.class, new Object[] {TriviaKind.SKIPPED_TEXT, MatcherBuilderUtils.convertToMatcherBuilder(element)});
  }

}
