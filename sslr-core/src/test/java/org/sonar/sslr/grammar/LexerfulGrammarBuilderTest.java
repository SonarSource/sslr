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

import com.sonar.sslr.api.Grammar;
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
import org.sonar.sslr.internal.grammar.LexerfulGrammarAdapter;
import org.sonar.sslr.internal.grammar.MatcherBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LexerfulGrammarBuilderTest {

  @Test
  public void should_have_no_definitions_at_first() {
    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.create().build()).ruleKeys()).isEmpty();
  }

  @Test
  public void should_allow_definitions_of_new_rules() {
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);

    LexerfulGrammarBuilder _ = LexerfulGrammarBuilder.create();

    GrammarRuleBuilder definition1 = _.rule(ruleKey1).is("foo");
    assertThat(((LexerfulGrammarAdapter) _.build()).ruleKeys()).containsOnly(ruleKey1);
    assertThat(_.rule(ruleKey1)).isSameAs(definition1);
    assertThat(((LexerfulGrammarAdapter) _.build()).ruleKeys()).containsOnly(ruleKey1);

    GrammarRuleBuilder definition2 = _.rule(ruleKey2).is("foo");
    assertThat(_.rule(ruleKey2)).isSameAs(definition2);
    assertThat(((LexerfulGrammarAdapter) _.build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2);
  }

  @Test
  public void should_base_on_other_grammars() {
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey3 = mock(GrammarRuleKey.class);

    LexerfulGrammarBuilder _1 = LexerfulGrammarBuilder.create();
    _1.rule(ruleKey1).is("foo");
    _1.rule(ruleKey2).is("foo");

    LexerfulGrammarBuilder _2 = LexerfulGrammarBuilder.create();
    _2.rule(ruleKey3).is("foo");

    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.createBasedOn(_1).build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2);
    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.createBasedOn(_2).build()).ruleKeys()).containsOnly(ruleKey3);
    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.createBasedOn(_1, _2).build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2, ruleKey3);
  }

  @Test
  public void should_have_memoization_disabled_by_default() {
    LexerfulGrammarBuilder _ = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    _.rule(ruleKey).is("foo");
    Grammar grammar = _.build();
    Matcher[] ruleMatchers = ((RuleDefinition) grammar.rule(ruleKey)).getRule().children;
    assertThat(ruleMatchers).hasSize(1);
    assertThat(ruleMatchers[0]).isInstanceOf(TokenValueMatcher.class);
  }

  @Test
  public void should_enable_memoization() {
    LexerfulGrammarBuilder _ = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    _.rule(ruleKey).is("foo");
    Grammar grammar = _.buildWithMemoizationOfMatchesForAllRules();
    Matcher[] ruleMatchers = ((RuleDefinition) grammar.rule(ruleKey)).getRule().children;
    assertThat(ruleMatchers).hasSize(1);
    assertThat(ruleMatchers[0]).isInstanceOf(MemoMatcher.class);
  }

  @Test
  public void should_build_grammar_instance() {
    assertThat(LexerfulGrammarBuilder.create().build()).isInstanceOf(LexerfulGrammarAdapter.class);
  }

  @Test
  public void matchers() {
    LexerfulGrammarBuilder _ = LexerfulGrammarBuilder.create();
    Grammar g = mock(Grammar.class);

    assertThat(((MatcherBuilder) _.sequence("foo", "bar")).build(g)).isInstanceOf(AndMatcher.class);
    assertThat(((MatcherBuilder) _.sequence("foo", "bar", "baz")).build(g)).isInstanceOf(AndMatcher.class);

    assertThat(((MatcherBuilder) _.firstOf("foo", "bar")).build(g)).isInstanceOf(OrMatcher.class);
    assertThat(((MatcherBuilder) _.firstOf("foo", "bar", "baz")).build(g)).isInstanceOf(OrMatcher.class);

    assertThat(((MatcherBuilder) _.optional("foo")).build(g)).isInstanceOf(OptMatcher.class);
    assertThat(((MatcherBuilder) _.optional("foo", "bar")).build(g)).isInstanceOf(OptMatcher.class);

    assertThat(((MatcherBuilder) _.oneOrMore("foo")).build(g)).isInstanceOf(OneToNMatcher.class);
    assertThat(((MatcherBuilder) _.oneOrMore("foo", "bar")).build(g)).isInstanceOf(OneToNMatcher.class);

    assertThat(((MatcherBuilder) _.zeroOrMore("foo")).build(g)).isInstanceOf(OptMatcher.class);
    assertThat(((MatcherBuilder) _.zeroOrMore("foo", "bar")).build(g)).isInstanceOf(OptMatcher.class);

    assertThat(((MatcherBuilder) _.next("foo")).build(g)).isInstanceOf(NextMatcher.class);
    assertThat(((MatcherBuilder) _.next("foo", "bar")).build(g)).isInstanceOf(NextMatcher.class);

    assertThat(((MatcherBuilder) _.nextNot("foo")).build(g)).isInstanceOf(NotMatcher.class);
    assertThat(((MatcherBuilder) _.nextNot("foo", "bar")).build(g)).isInstanceOf(NotMatcher.class);

    assertThat(((MatcherBuilder) _.nothing()).build(g)).isInstanceOf(BooleanMatcher.class);
    assertThat(((MatcherBuilder) _.adjacent("foo")).build(g)).isInstanceOf(AdjacentMatcher.class);
    assertThat(((MatcherBuilder) _.anyTokenButNot("foo")).build(g)).isInstanceOf(AnyTokenButNotMatcher.class);
    assertThat(((MatcherBuilder) _.isOneOfThem(mock(TokenType.class))).build(g)).isInstanceOf(TokenTypesMatcher.class);
    assertThat(((MatcherBuilder) _.bridge(mock(TokenType.class), mock(TokenType.class))).build(g)).isInstanceOf(BridgeMatcher.class);
    assertThat(((MatcherBuilder) _.everything()).build(g)).isInstanceOf(BooleanMatcher.class);
    assertThat(((MatcherBuilder) _.anyToken()).build(g)).isInstanceOf(AnyTokenMatcher.class);
    assertThat(((MatcherBuilder) _.tillNewLine()).build(g)).isInstanceOf(TillNewLineMatcher.class);
    assertThat(((MatcherBuilder) _.till("foo")).build(g)).isInstanceOf(InclusiveTillMatcher.class);
    assertThat(((MatcherBuilder) _.exclusiveTill("foo")).build(g)).isInstanceOf(ExclusiveTillMatcher.class);
  }

}
