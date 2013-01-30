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
import com.sonar.sslr.api.Grammar;
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
import org.sonar.sslr.internal.grammar.LexerfulGrammarAdapter;
import org.sonar.sslr.internal.grammar.LexerfulGrammarRuleDefinition;
import org.sonar.sslr.internal.grammar.MatcherBuilderUtils;
import org.sonar.sslr.internal.grammar.ReflexiveMatcherBuilder;

import java.util.Arrays;
import java.util.Map;

/**
 * A builder for creating grammars for lexerful parsing.
 *
 * @since 1.18
 */
public class LexerfulGrammarBuilder {

  private final Map<GrammarRuleKey, LexerfulGrammarRuleDefinition> definitions = Maps.newHashMap();
  private GrammarRuleKey rootRuleKey;

  public static LexerfulGrammarBuilder create() {
    return new LexerfulGrammarBuilder();
  }

  public static LexerfulGrammarBuilder createBasedOn(LexerfulGrammarBuilder... base) {
    return new LexerfulGrammarBuilder(base);
  }

  private LexerfulGrammarBuilder(LexerfulGrammarBuilder... base) {
    for (LexerfulGrammarBuilder b : base) {
      definitions.putAll(b.definitions);
    }
  }

  /**
   * Allows to describe rule.
   */
  public GrammarRuleBuilder rule(GrammarRuleKey ruleKey) {
    LexerfulGrammarRuleDefinition definition = definitions.get(ruleKey);
    if (definition == null) {
      definition = new LexerfulGrammarRuleDefinition(ruleKey);
      definitions.put(ruleKey, definition);
    }
    return definition;
  }

  /**
   * Allows to specify that given rule should be root for grammar.
   */
  public void setRootRule(GrammarRuleKey ruleKey) {
    rule(ruleKey);
    rootRuleKey = ruleKey;
  }

  /**
   * Constructs grammar.
   *
   * @throws GrammarException if some of rules were used, but not defined
   * @return grammar
   * @see #buildWithMemoizationOfMatchesForAllRules()
   */
  public Grammar build() {
    return new LexerfulGrammarAdapter(definitions.values(), rootRuleKey, false);
  }

  /**
   * Constructs grammar with memoization of matches for all rules.
   *
   * @throws GrammarException if some of rules were used, but not defined
   * @return grammar
   * @see #build()
   */
  public Grammar buildWithMemoizationOfMatchesForAllRules() {
    return new LexerfulGrammarAdapter(definitions.values(), rootRuleKey, true);
  }

  public Object sequence(Object e1, Object e2) {
    return MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Arrays.asList(e1, e2));
  }

  public Object sequence(Object e1, Object e2, Object... others) {
    return MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, e2, others));
  }

  public Object firstOf(Object e1, Object e2) {
    return new ReflexiveMatcherBuilder(OrMatcher.class, MatcherBuilderUtils.lexerfulToMatcherBuilders(Arrays.asList(e1, e2)));
  }

  public Object firstOf(Object e1, Object e2, Object... others) {
    return new ReflexiveMatcherBuilder(OrMatcher.class, MatcherBuilderUtils.lexerfulToMatcherBuilders(Lists.asList(e1, e2, others)));
  }

  public Object optional(Object e1) {
    return new ReflexiveMatcherBuilder(OptMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Arrays.asList(e1))});
  }

  public Object optional(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(OptMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object oneOrMore(Object e1) {
    return new ReflexiveMatcherBuilder(OneToNMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Arrays.asList(e1))});
  }

  public Object oneOrMore(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(OneToNMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object zeroOrMore(Object e1) {
    return optional(new ReflexiveMatcherBuilder(OneToNMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Arrays.asList(e1))}));
  }

  public Object zeroOrMore(Object e1, Object... others) {
    return optional(new ReflexiveMatcherBuilder(OneToNMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))}));
  }

  public Object next(Object e1) {
    return new ReflexiveMatcherBuilder(NextMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Arrays.asList(e1))});
  }

  public Object next(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(NextMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object nextNot(Object e1) {
    return new ReflexiveMatcherBuilder(NotMatcher.class, new Object[] {MatcherBuilderUtils.lexerfulToSingleMatcherBuilder(Arrays.asList(e1))});
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
