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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.AdjacentMatcher;
import com.sonar.sslr.impl.matcher.AnyTokenButNotMatcher;
import com.sonar.sslr.impl.matcher.AnyTokenMatcher;
import com.sonar.sslr.impl.matcher.BooleanMatcher;
import com.sonar.sslr.impl.matcher.BridgeMatcher;
import com.sonar.sslr.impl.matcher.ExclusiveTillMatcher;
import com.sonar.sslr.impl.matcher.InclusiveTillMatcher;
import com.sonar.sslr.impl.matcher.NextMatcher;
import com.sonar.sslr.impl.matcher.NotMatcher;
import com.sonar.sslr.impl.matcher.OneToNMatcher;
import com.sonar.sslr.impl.matcher.OptMatcher;
import com.sonar.sslr.impl.matcher.OrMatcher;
import com.sonar.sslr.impl.matcher.TillNewLineMatcher;
import com.sonar.sslr.impl.matcher.TokenTypesMatcher;
import org.sonar.sslr.internal.grammar.LexerfulGrammar;
import org.sonar.sslr.internal.grammar.MatcherBuilderUtils;
import org.sonar.sslr.internal.grammar.ReflexiveMatcherBuilder;

import java.util.Collection;
import java.util.Map;

public class LexerfulGrammarBuilder {

  private final Map<GrammarRule, LexerfulGrammarRuleDefinition> definitions = Maps.newHashMap();
  private boolean enableMemoizationOfMatchesForAllRules = false;

  public LexerfulGrammarBuilder basedOn(LexerfulGrammarBuilder otherGrammar) {
    this.definitions.putAll(otherGrammar.definitions);
    return this;
  }

  public LexerfulGrammarRuleDefinition rule(GrammarRule rule) {
    LexerfulGrammarRuleDefinition definition = definitions.get(rule);
    if (definition == null) {
      definition = new LexerfulGrammarRuleDefinition(rule);
      definitions.put(rule, definition);
    }

    return definition;
  }

  public Collection<LexerfulGrammarRuleDefinition> rules() {
    return definitions.values();
  }

  public LexerfulGrammarBuilder enableMemoizationOfMatchesForAllRules() {
    this.enableMemoizationOfMatchesForAllRules = true;
    return this;
  }

  public boolean isMemoizationOfMatchedForAllRulesEnabled() {
    return enableMemoizationOfMatchesForAllRules;
  }

  public Grammar build() {
    return new LexerfulGrammar(this);
  }

  public Object sequence(Object e1, Object e2, Object... others) {
    return MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, e2, others));
  }

  public Object firstOf(Object e1, Object e2, Object... others) {
    return new ReflexiveMatcherBuilder(OrMatcher.class, MatcherBuilderUtils.lexerfulToMatcherBuilders(Lists.asList(e1, e2, others)));
  }

  public Object optional(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(OptMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object oneOrMore(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(OneToNMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object zeroOrMore(Object e1, Object... others) {
    return optional(new ReflexiveMatcherBuilder(OneToNMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))}));
  }

  public Object next(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(NextMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object nextNot(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(NotMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object nothing() {
    return new ReflexiveMatcherBuilder(BooleanMatcher.class, new Object[] {false});
  }

  public Object adjacent(Object element) {
    return new ReflexiveMatcherBuilder(AdjacentMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToMatcherBuilder(element)});
  }

  public Object anyTokenButNot(Object element) {
    return new ReflexiveMatcherBuilder(AnyTokenButNotMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToMatcherBuilder(element)});
  }

  public Object isOneOfThem(TokenType t1, TokenType... others) {
    TokenType[] types = new TokenType[1 + others.length];
    types[0] = t1;
    System.arraycopy(others, 0, types, 1, others.length);
    return new ReflexiveMatcherBuilder(TokenTypesMatcher.class, new Object[] {types});
  }

  public Object bridge(TokenType from, TokenType to) {
    return new ReflexiveMatcherBuilder(BridgeMatcher.class, new Object[] {from, to});
  }

  public Object everything() {
    return new ReflexiveMatcherBuilder(BooleanMatcher.class, new Object[] {true});
  }

  public Object anyToken() {
    return new ReflexiveMatcherBuilder(AnyTokenMatcher.class, new Object[0]);
  }

  public Object tillNewLine() {
    return new ReflexiveMatcherBuilder(TillNewLineMatcher.class, new Object[0]);
  }

  public Object till(Object element) {
    return new ReflexiveMatcherBuilder(InclusiveTillMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToMatcherBuilder(element)});
  }

  public Object exclusiveTill(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(ExclusiveTillMatcher.class, MatcherBuilderUtils.lexerfulToMatcherBuilders(Lists.asList(e1, others)));
  }

}
