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

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.AdjacentMatcher;
import com.sonar.sslr.impl.matcher.AndMatcher;
import com.sonar.sslr.impl.matcher.AnyTokenButNotMatcher;
import com.sonar.sslr.impl.matcher.AnyTokenMatcher;
import com.sonar.sslr.impl.matcher.BooleanMatcher;
import com.sonar.sslr.impl.matcher.BridgeMatcher;
import com.sonar.sslr.impl.matcher.ExclusiveTillMatcher;
import com.sonar.sslr.impl.matcher.InclusiveTillMatcher;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoMatcher;
import com.sonar.sslr.impl.matcher.NextMatcher;
import com.sonar.sslr.impl.matcher.NotMatcher;
import com.sonar.sslr.impl.matcher.OneToNMatcher;
import com.sonar.sslr.impl.matcher.OptMatcher;
import com.sonar.sslr.impl.matcher.OrMatcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.TillNewLineMatcher;
import com.sonar.sslr.impl.matcher.TokenTypesMatcher;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;
import org.junit.Test;
import org.sonar.sslr.internal.grammar.LexerfulGrammar;
import org.sonar.sslr.internal.grammar.MatcherBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LexerfulGrammarBuilderTest {

  @Test
  public void should_have_no_definitions_at_first() {
    assertThat(new LexerfulGrammarBuilder().rules()).isEmpty();
  }

  @Test
  public void should_allow_definitions_of_new_rules() {
    LexerfulGrammarBuilder _ = new LexerfulGrammarBuilder();
    GrammarRule rule1 = mock(GrammarRule.class);
    GrammarRule rule2 = mock(GrammarRule.class);

    LexerfulGrammarRuleDefinition definition1 = _.rule(rule1);
    assertThat(_.rules()).containsOnly(definition1);
    assertThat(_.rule(rule1)).isSameAs(definition1);
    assertThat(_.rules()).containsOnly(definition1);

    LexerfulGrammarRuleDefinition definition2 = _.rule(rule2);
    assertThat(_.rules()).containsOnly(definition1, definition2);
  }

  @Test
  public void should_base_on_other_grammars() {
    LexerfulGrammarBuilder _1 = new LexerfulGrammarBuilder();
    LexerfulGrammarRuleDefinition definition1 = _1.rule(mock(GrammarRule.class));
    LexerfulGrammarRuleDefinition definition2 = _1.rule(mock(GrammarRule.class));

    LexerfulGrammarBuilder _2 = new LexerfulGrammarBuilder();
    LexerfulGrammarRuleDefinition definition3 = _2.rule(mock(GrammarRule.class));

    LexerfulGrammarBuilder _ = new LexerfulGrammarBuilder();

    _.basedOn(_1);
    assertThat(_.rules()).containsOnly(definition1, definition2);

    _.basedOn(_2);
    assertThat(_.rules()).containsOnly(definition1, definition2, definition3);
  }

  @Test
  public void should_have_memoization_disabled_by_default() {
    LexerfulGrammarBuilder _ = new LexerfulGrammarBuilder();
    GrammarRule rule = mock(GrammarRule.class);
    _.rule(rule).is("foo");
    Grammar grammar = _.build();
    Matcher[] ruleMatchers = ((RuleDefinition) grammar.rule(rule)).getRule().children;
    assertThat(ruleMatchers).hasSize(1);
    assertThat(ruleMatchers[0]).isInstanceOf(TokenValueMatcher.class);
  }

  @Test
  public void should_enable_memoization() {
    LexerfulGrammarBuilder _ = new LexerfulGrammarBuilder();
    GrammarRule rule = mock(GrammarRule.class);
    _.rule(rule).is("foo");
    Grammar grammar = _.buildWithMemoizationOfMatchesForAllRules();
    Matcher[] ruleMatchers = ((RuleDefinition) grammar.rule(rule)).getRule().children;
    assertThat(ruleMatchers).hasSize(1);
    assertThat(ruleMatchers[0]).isInstanceOf(MemoMatcher.class);
  }

  @Test
  public void should_build_grammar_instance() {
    assertThat(new LexerfulGrammarBuilder().build()).isInstanceOf(LexerfulGrammar.class);
  }

  @Test
  public void matchers() {
    LexerfulGrammarBuilder _ = new LexerfulGrammarBuilder();

    assertThat(((MatcherBuilder) _.sequence("foo", "bar")).build(mock(Grammar.class))).isInstanceOf(AndMatcher.class);
    assertThat(((MatcherBuilder) _.firstOf("foo", "bar")).build(mock(Grammar.class))).isInstanceOf(OrMatcher.class);
    assertThat(((MatcherBuilder) _.optional("foo")).build(mock(Grammar.class))).isInstanceOf(OptMatcher.class);
    assertThat(((MatcherBuilder) _.oneOrMore("foo")).build(mock(Grammar.class))).isInstanceOf(OneToNMatcher.class);
    assertThat(((MatcherBuilder) _.zeroOrMore("foo")).build(mock(Grammar.class))).isInstanceOf(OptMatcher.class);
    assertThat(((MatcherBuilder) _.next("foo")).build(mock(Grammar.class))).isInstanceOf(NextMatcher.class);
    assertThat(((MatcherBuilder) _.nextNot("foo")).build(mock(Grammar.class))).isInstanceOf(NotMatcher.class);
    assertThat(((MatcherBuilder) _.nothing()).build(mock(Grammar.class))).isInstanceOf(BooleanMatcher.class);
    assertThat(((MatcherBuilder) _.adjacent("foo")).build(mock(Grammar.class))).isInstanceOf(AdjacentMatcher.class);
    assertThat(((MatcherBuilder) _.anyTokenButNot("foo")).build(mock(Grammar.class))).isInstanceOf(AnyTokenButNotMatcher.class);
    assertThat(((MatcherBuilder) _.isOneOfThem(mock(TokenType.class))).build(mock(Grammar.class))).isInstanceOf(TokenTypesMatcher.class);
    assertThat(((MatcherBuilder) _.bridge(mock(TokenType.class), mock(TokenType.class))).build(mock(Grammar.class))).isInstanceOf(BridgeMatcher.class);
    assertThat(((MatcherBuilder) _.everything()).build(mock(Grammar.class))).isInstanceOf(BooleanMatcher.class);
    assertThat(((MatcherBuilder) _.anyToken()).build(mock(Grammar.class))).isInstanceOf(AnyTokenMatcher.class);
    assertThat(((MatcherBuilder) _.tillNewLine()).build(mock(Grammar.class))).isInstanceOf(TillNewLineMatcher.class);
    assertThat(((MatcherBuilder) _.till("foo")).build(mock(Grammar.class))).isInstanceOf(InclusiveTillMatcher.class);
    assertThat(((MatcherBuilder) _.exclusiveTill("foo")).build(mock(Grammar.class))).isInstanceOf(ExclusiveTillMatcher.class);
  }

}
