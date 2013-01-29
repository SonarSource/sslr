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
import org.junit.Test;
import org.sonar.sslr.internal.grammar.LexerlessGrammarAdapter;
import org.sonar.sslr.internal.grammar.MatcherBuilder;
import org.sonar.sslr.internal.matchers.EndOfInputMatcher;
import org.sonar.sslr.internal.matchers.FirstOfMatcher;
import org.sonar.sslr.internal.matchers.NothingMatcher;
import org.sonar.sslr.internal.matchers.OneOrMoreMatcher;
import org.sonar.sslr.internal.matchers.OptionalMatcher;
import org.sonar.sslr.internal.matchers.PatternMatcher;
import org.sonar.sslr.internal.matchers.SequenceMatcher;
import org.sonar.sslr.internal.matchers.TestMatcher;
import org.sonar.sslr.internal.matchers.TestNotMatcher;
import org.sonar.sslr.internal.matchers.TokenMatcher;
import org.sonar.sslr.internal.matchers.TriviaMatcher;
import org.sonar.sslr.internal.matchers.ZeroOrMoreMatcher;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LexerlessGrammarBuilderTest {

  @Test
  public void should_have_no_definitions_at_first() {
    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.create().build()).rules()).isEmpty();
  }

  @Test
  public void should_allow_definitions_of_new_rules() {
    GrammarRule rule1 = mock(GrammarRule.class);
    GrammarRule rule2 = mock(GrammarRule.class);

    LexerlessGrammarBuilder _ = LexerlessGrammarBuilder.create();

    GrammarRuleBuilder definition1 = _.rule(rule1).is("foo");
    assertThat(((LexerlessGrammarAdapter) _.build()).rules()).containsOnly(rule1);
    assertThat(_.rule(rule1)).isSameAs(definition1);
    assertThat(((LexerlessGrammarAdapter) _.build()).rules()).containsOnly(rule1);

    GrammarRuleBuilder definition2 = _.rule(rule2).is("foo");
    assertThat(_.rule(rule2)).isSameAs(definition2);
    assertThat(((LexerlessGrammarAdapter) _.build()).rules()).containsOnly(rule1, rule2);
  }

  @Test
  public void should_base_on_other_grammars() {
    GrammarRule rule1 = mock(GrammarRule.class);
    GrammarRule rule2 = mock(GrammarRule.class);
    GrammarRule rule3 = mock(GrammarRule.class);

    LexerlessGrammarBuilder _1 = LexerlessGrammarBuilder.create();
    _1.rule(rule1).is("foo");
    _1.rule(rule2).is("foo");

    LexerlessGrammarBuilder _2 = LexerlessGrammarBuilder.create();
    _2.rule(rule3).is("foo");

    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.createBasedOn(_1).build()).rules()).containsOnly(rule1, rule2);
    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.createBasedOn(_2).build()).rules()).containsOnly(rule3);
    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.createBasedOn(_1, _2).build()).rules()).containsOnly(rule1, rule2, rule3);
  }

  @Test
  public void should_build_grammar_instance() {
    assertThat(LexerlessGrammarBuilder.create().build()).isInstanceOf(LexerlessGrammarAdapter.class);
  }

  @Test
  public void matchers() {
    LexerlessGrammarBuilder _ = LexerlessGrammarBuilder.create();

    assertThat(((MatcherBuilder) _.sequence("foo", "bar")).build(mock(Grammar.class))).isInstanceOf(SequenceMatcher.class);
    assertThat(((MatcherBuilder) _.firstOf("foo", "bar")).build(mock(Grammar.class))).isInstanceOf(FirstOfMatcher.class);
    assertThat(((MatcherBuilder) _.optional("foo")).build(mock(Grammar.class))).isInstanceOf(OptionalMatcher.class);
    assertThat(((MatcherBuilder) _.oneOrMore("foo")).build(mock(Grammar.class))).isInstanceOf(OneOrMoreMatcher.class);
    assertThat(((MatcherBuilder) _.zeroOrMore("foo")).build(mock(Grammar.class))).isInstanceOf(ZeroOrMoreMatcher.class);
    assertThat(((MatcherBuilder) _.next("foo")).build(mock(Grammar.class))).isInstanceOf(TestMatcher.class);
    assertThat(((MatcherBuilder) _.nextNot("foo")).build(mock(Grammar.class))).isInstanceOf(TestNotMatcher.class);
    assertThat(((MatcherBuilder) _.nothing()).build(mock(Grammar.class))).isInstanceOf(NothingMatcher.class);
    assertThat(((MatcherBuilder) _.regexp("foo")).build(mock(Grammar.class))).isInstanceOf(PatternMatcher.class);
    assertThat(((MatcherBuilder) _.endOfInput()).build(mock(Grammar.class))).isInstanceOf(EndOfInputMatcher.class);
    assertThat(((MatcherBuilder) _.token(mock(TokenType.class), "foo")).build(mock(Grammar.class))).isInstanceOf(TokenMatcher.class);
    assertThat(((MatcherBuilder) _.commentTrivia("foo")).build(mock(Grammar.class))).isInstanceOf(TriviaMatcher.class);
    assertThat(((MatcherBuilder) _.skippedTrivia("foo")).build(mock(Grammar.class))).isInstanceOf(TriviaMatcher.class);
  }

}
