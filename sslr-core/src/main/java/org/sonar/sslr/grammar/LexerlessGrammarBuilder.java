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
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.sonar.sslr.internal.grammar.LexerlessGrammarAdapter;
import org.sonar.sslr.internal.grammar.LexerlessGrammarRuleDefinition;
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
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.Map;

/**
 * A builder for creating grammars for lexerless parsing.
 *
 * @since 1.18
 */
public class LexerlessGrammarBuilder {

  private final Map<GrammarRule, LexerlessGrammarRuleDefinition> definitions = Maps.newHashMap();
  private GrammarRule rootRule;

  public static LexerlessGrammarBuilder create() {
    return new LexerlessGrammarBuilder();
  }

  public static LexerlessGrammarBuilder createBasedOn(LexerlessGrammarBuilder... base) {
    return new LexerlessGrammarBuilder(base);
  }

  private LexerlessGrammarBuilder(LexerlessGrammarBuilder... base) {
    for (LexerlessGrammarBuilder b : base) {
      definitions.putAll(b.definitions);
    }
  }

  public GrammarRuleBuilder rule(GrammarRule rule) {
    LexerlessGrammarRuleDefinition definition = definitions.get(rule);
    if (definition == null) {
      definition = new LexerlessGrammarRuleDefinition(rule);
      definitions.put(rule, definition);
    }
    return definition;
  }

  public void setRootRule(GrammarRule rule) {
    rootRule = rule;
  }

  /**
   * Constructs grammar.
   */
  public LexerlessGrammar build() {
    return new LexerlessGrammarAdapter(this, definitions.values(), rootRule);
  }

  public Object sequence(Object e1, Object e2, Object... others) {
    return MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, e2, others));
  }

  public Object firstOf(Object e1, Object e2, Object... others) {
    return new ReflexiveMatcherBuilder(FirstOfMatcher.class, MatcherBuilderUtils.lexerlessToMatcherBuilders(Lists.asList(e1, e2, others)));
  }

  public Object optional(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(OptionalMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object oneOrMore(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(OneOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object zeroOrMore(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(ZeroOrMoreMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object next(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(TestMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object nextNot(Object e1, Object... others) {
    return new ReflexiveMatcherBuilder(TestNotMatcher.class, new Object[] {MatcherBuilderUtils.lexerlessToSingleMatcherBuilder(Lists.asList(e1, others))});
  }

  public Object nothing() {
    return new ReflexiveMatcherBuilder(NothingMatcher.class, new Object[0]);
  }

  public Object regexp(String regexp) {
    return new ReflexiveMatcherBuilder(PatternMatcher.class, new Object[] {regexp});
  }

  public Object endOfInput() {
    return new ReflexiveMatcherBuilder(EndOfInputMatcher.class, new Object[0]);
  }

  public Object token(TokenType tokenType, Object element) {
    return new ReflexiveMatcherBuilder(TokenMatcher.class, new Object[] {tokenType, MatcherBuilderUtils.lexerlessToMatcherBuilder(element)});
  }

  public Object commentTrivia(Object element) {
    return new ReflexiveMatcherBuilder(TriviaMatcher.class, new Object[] {TriviaKind.COMMENT, MatcherBuilderUtils.lexerlessToMatcherBuilder(element)});
  }

  public Object skippedTrivia(Object element) {
    return new ReflexiveMatcherBuilder(TriviaMatcher.class, new Object[] {TriviaKind.SKIPPED_TEXT, MatcherBuilderUtils.lexerlessToMatcherBuilder(element)});
  }

}
