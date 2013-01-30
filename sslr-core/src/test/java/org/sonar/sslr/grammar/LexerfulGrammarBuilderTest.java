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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.grammar.LexerfulGrammarAdapter;
import org.sonar.sslr.internal.grammar.MatcherBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LexerfulGrammarBuilderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_have_no_definitions_at_first() {
    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.create().build()).ruleKeys()).isEmpty();
  }

  @Test
  public void should_allow_definitions_of_new_rules() {
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);

    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

    GrammarRuleBuilder definition1 = b.rule(ruleKey1).is("foo");
    assertThat(((LexerfulGrammarAdapter) b.build()).ruleKeys()).containsOnly(ruleKey1);
    assertThat(b.rule(ruleKey1)).isSameAs(definition1);
    assertThat(((LexerfulGrammarAdapter) b.build()).ruleKeys()).containsOnly(ruleKey1);

    GrammarRuleBuilder definition2 = b.rule(ruleKey2).is("foo");
    assertThat(b.rule(ruleKey2)).isSameAs(definition2);
    assertThat(((LexerfulGrammarAdapter) b.build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2);
  }

  @Test
  public void should_base_on_other_grammars() {
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey3 = mock(GrammarRuleKey.class);

    LexerfulGrammarBuilder b1 = LexerfulGrammarBuilder.create();
    b1.rule(ruleKey1).is("foo");
    b1.rule(ruleKey2).is("foo");

    LexerfulGrammarBuilder b2 = LexerfulGrammarBuilder.create();
    b2.rule(ruleKey3).is("foo");

    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.createBasedOn(b1).build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2);
    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.createBasedOn(b2).build()).ruleKeys()).containsOnly(ruleKey3);
    assertThat(((LexerfulGrammarAdapter) LexerfulGrammarBuilder.createBasedOn(b1, b2).build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2, ruleKey3);
  }

  @Test
  public void should_have_memoization_disabled_by_default() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    Grammar grammar = b.build();
    Matcher[] ruleMatchers = ((RuleDefinition) grammar.rule(ruleKey)).getRule().children;
    assertThat(ruleMatchers).hasSize(1);
    assertThat(ruleMatchers[0]).isInstanceOf(TokenValueMatcher.class);
  }

  @Test
  public void should_enable_memoization() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    Grammar grammar = b.buildWithMemoizationOfMatchesForAllRules();
    Matcher[] ruleMatchers = ((RuleDefinition) grammar.rule(ruleKey)).getRule().children;
    assertThat(ruleMatchers).hasSize(1);
    assertThat(ruleMatchers[0]).isInstanceOf(MemoMatcher.class);
  }

  @Test
  public void should_create_grammar_with_root_rule() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    b.setRootRule(ruleKey);
    Grammar g = b.build();
    assertThat(g.getRootRule()).isNotNull().isSameAs(g.rule(ruleKey));
  }

  @Test
  public void should_create_grammar_without_root_rule() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    assertThat(b.build().getRootRule()).isNull();
  }

  @Test
  public void should_throw_exception_when_root_rule_not_defined() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    when(ruleKey.toString()).thenReturn("name");
    b.setRootRule(ruleKey);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule 'name' hasn't beed defined.");
    b.build();
  }

  @Test
  public void should_build_grammar_instance() {
    assertThat(LexerfulGrammarBuilder.create().build()).isInstanceOf(LexerfulGrammarAdapter.class);
  }

  @Test
  public void matchers() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    Grammar g = mock(Grammar.class);

    assertThat(((MatcherBuilder) b.sequence("foo", "bar")).build(g)).isInstanceOf(AndMatcher.class);
    assertThat(((MatcherBuilder) b.sequence("foo", "bar", "baz")).build(g)).isInstanceOf(AndMatcher.class);

    assertThat(((MatcherBuilder) b.firstOf("foo", "bar")).build(g)).isInstanceOf(OrMatcher.class);
    assertThat(((MatcherBuilder) b.firstOf("foo", "bar", "baz")).build(g)).isInstanceOf(OrMatcher.class);

    assertThat(((MatcherBuilder) b.optional("foo")).build(g)).isInstanceOf(OptMatcher.class);
    assertThat(((MatcherBuilder) b.optional("foo", "bar")).build(g)).isInstanceOf(OptMatcher.class);

    assertThat(((MatcherBuilder) b.oneOrMore("foo")).build(g)).isInstanceOf(OneToNMatcher.class);
    assertThat(((MatcherBuilder) b.oneOrMore("foo", "bar")).build(g)).isInstanceOf(OneToNMatcher.class);

    assertThat(((MatcherBuilder) b.zeroOrMore("foo")).build(g)).isInstanceOf(OptMatcher.class);
    assertThat(((MatcherBuilder) b.zeroOrMore("foo", "bar")).build(g)).isInstanceOf(OptMatcher.class);

    assertThat(((MatcherBuilder) b.next("foo")).build(g)).isInstanceOf(NextMatcher.class);
    assertThat(((MatcherBuilder) b.next("foo", "bar")).build(g)).isInstanceOf(NextMatcher.class);

    assertThat(((MatcherBuilder) b.nextNot("foo")).build(g)).isInstanceOf(NotMatcher.class);
    assertThat(((MatcherBuilder) b.nextNot("foo", "bar")).build(g)).isInstanceOf(NotMatcher.class);

    assertThat(((MatcherBuilder) b.nothing()).build(g)).isInstanceOf(BooleanMatcher.class);
    assertThat(((MatcherBuilder) b.adjacent("foo")).build(g)).isInstanceOf(AdjacentMatcher.class);
    assertThat(((MatcherBuilder) b.anyTokenButNot("foo")).build(g)).isInstanceOf(AnyTokenButNotMatcher.class);
    assertThat(((MatcherBuilder) b.isOneOfThem(mock(TokenType.class))).build(g)).isInstanceOf(TokenTypesMatcher.class);
    assertThat(((MatcherBuilder) b.bridge(mock(TokenType.class), mock(TokenType.class))).build(g)).isInstanceOf(BridgeMatcher.class);
    assertThat(((MatcherBuilder) b.everything()).build(g)).isInstanceOf(BooleanMatcher.class);
    assertThat(((MatcherBuilder) b.anyToken()).build(g)).isInstanceOf(AnyTokenMatcher.class);
    assertThat(((MatcherBuilder) b.tillNewLine()).build(g)).isInstanceOf(TillNewLineMatcher.class);
    assertThat(((MatcherBuilder) b.till("foo")).build(g)).isInstanceOf(InclusiveTillMatcher.class);
    assertThat(((MatcherBuilder) b.exclusiveTill("foo")).build(g)).isInstanceOf(ExclusiveTillMatcher.class);
  }

}
