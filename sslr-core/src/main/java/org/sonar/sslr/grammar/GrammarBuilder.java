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
import org.sonar.sslr.impl.grammar.EndOfInputMatcherBuilder;
import org.sonar.sslr.impl.grammar.FirstOfBuilder;
import org.sonar.sslr.impl.grammar.MatcherBuilderUtils;
import org.sonar.sslr.impl.grammar.NextMatcherBuilder;
import org.sonar.sslr.impl.grammar.NextNotMatcherBuilder;
import org.sonar.sslr.impl.grammar.NothingMatcherBuilder;
import org.sonar.sslr.impl.grammar.OneOrMoreMatcherBuilder;
import org.sonar.sslr.impl.grammar.OptionalMatcherBuilder;
import org.sonar.sslr.impl.grammar.PatternMatcherBuilder;
import org.sonar.sslr.impl.grammar.TokenMatcherBuilder;
import org.sonar.sslr.impl.grammar.TriviaMatcherBuilder;
import org.sonar.sslr.impl.grammar.ZeroOrMoreMatcherBuilder;

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
    return new FirstOfBuilder(MatcherBuilderUtils.convertToMatcherBuilders(elements));
  }

  public Object optional(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new OptionalMatcherBuilder(MatcherBuilderUtils.convertToSingleMatcherBuilder(elements));
  }

  public Object oneOrMore(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new OneOrMoreMatcherBuilder(MatcherBuilderUtils.convertToSingleMatcherBuilder(elements));
  }

  public Object zeroOrMore(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new ZeroOrMoreMatcherBuilder(MatcherBuilderUtils.convertToSingleMatcherBuilder(elements));
  }

  public Object next(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new NextMatcherBuilder(MatcherBuilderUtils.convertToSingleMatcherBuilder(elements));
  }

  public Object nextNot(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    return new NextNotMatcherBuilder(MatcherBuilderUtils.convertToSingleMatcherBuilder(elements));
  }

  // lexerless
  public Object regexp(String regexp) {
    return new PatternMatcherBuilder(regexp);
  }

  // lexerless
  public Object endOfInput() {
    return new EndOfInputMatcherBuilder();
  }

  // lexerless
  public Object nothing(String regexp) {
    return new NothingMatcherBuilder();
  }

  // lexerless
  public Object token(TokenType tokenType, Object element) {
    return new TokenMatcherBuilder(tokenType, MatcherBuilderUtils.convertToMatcherBuilder(element));
  }

  // lexerless
  public Object commentTrivia(Object element) {
    return new TriviaMatcherBuilder(TriviaKind.COMMENT, MatcherBuilderUtils.convertToMatcherBuilder(element));
  }

  // lexerless
  public Object skippedTrivia(Object element) {
    return new TriviaMatcherBuilder(TriviaKind.SKIPPED_TEXT, MatcherBuilderUtils.convertToMatcherBuilder(element));
  }

}
