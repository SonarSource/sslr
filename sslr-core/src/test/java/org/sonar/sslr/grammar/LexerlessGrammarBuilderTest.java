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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

import java.util.regex.PatternSyntaxException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LexerlessGrammarBuilderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test_wrong_regexp() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    thrown.expect(PatternSyntaxException.class);
    b.regexp("[");
  }

  @Test
  public void should_have_no_definitions_at_first() {
    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.create().build()).ruleKeys()).isEmpty();
  }

  @Test
  public void should_allow_definitions_of_new_rules() {
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);

    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    GrammarRuleBuilder definition1 = b.rule(ruleKey1).is("foo");
    assertThat(((LexerlessGrammarAdapter) b.build()).ruleKeys()).containsOnly(ruleKey1);
    assertThat(b.rule(ruleKey1)).isSameAs(definition1);
    assertThat(((LexerlessGrammarAdapter) b.build()).ruleKeys()).containsOnly(ruleKey1);

    GrammarRuleBuilder definition2 = b.rule(ruleKey2).is("foo");
    assertThat(b.rule(ruleKey2)).isSameAs(definition2);
    assertThat(((LexerlessGrammarAdapter) b.build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2);
  }

  @Test
  public void should_base_on_other_grammars() {
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey3 = mock(GrammarRuleKey.class);

    LexerlessGrammarBuilder _1 = LexerlessGrammarBuilder.create();
    _1.rule(ruleKey1).is("foo");
    _1.rule(ruleKey2).is("foo");

    LexerlessGrammarBuilder _2 = LexerlessGrammarBuilder.create();
    _2.rule(ruleKey3).is("foo");

    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.createBasedOn(_1).build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2);
    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.createBasedOn(_2).build()).ruleKeys()).containsOnly(ruleKey3);
    assertThat(((LexerlessGrammarAdapter) LexerlessGrammarBuilder.createBasedOn(_1, _2).build()).ruleKeys()).containsOnly(ruleKey1, ruleKey2, ruleKey3);
  }

  @Test
  public void should_build_grammar_instance() {
    assertThat(LexerlessGrammarBuilder.create().build()).isInstanceOf(LexerlessGrammarAdapter.class);
  }

  @Test
  public void should_create_grammar_with_root_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
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
  public void should_throw_an_exception_when_root_rule_not_defined() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    when(ruleKey.toString()).thenReturn("name");
    b.setRootRule(ruleKey);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule 'name' hasn't beed defined.");
    b.build();
  }

  @Test
  public void matchers() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    Grammar g = mock(Grammar.class);

    assertThat(((MatcherBuilder) b.sequence("foo", "bar")).build(g)).isInstanceOf(SequenceMatcher.class);
    assertThat(((MatcherBuilder) b.sequence("foo", "bar", "baz")).build(g)).isInstanceOf(SequenceMatcher.class);

    assertThat(((MatcherBuilder) b.firstOf("foo", "bar")).build(g)).isInstanceOf(FirstOfMatcher.class);
    assertThat(((MatcherBuilder) b.firstOf("foo", "bar", "baz")).build(g)).isInstanceOf(FirstOfMatcher.class);

    assertThat(((MatcherBuilder) b.optional("foo")).build(g)).isInstanceOf(OptionalMatcher.class);
    assertThat(((MatcherBuilder) b.optional("foo", "bar")).build(g)).isInstanceOf(OptionalMatcher.class);

    assertThat(((MatcherBuilder) b.oneOrMore("foo")).build(g)).isInstanceOf(OneOrMoreMatcher.class);
    assertThat(((MatcherBuilder) b.oneOrMore("foo", "bar")).build(g)).isInstanceOf(OneOrMoreMatcher.class);

    assertThat(((MatcherBuilder) b.zeroOrMore("foo")).build(g)).isInstanceOf(ZeroOrMoreMatcher.class);
    assertThat(((MatcherBuilder) b.zeroOrMore("foo", "bar")).build(g)).isInstanceOf(ZeroOrMoreMatcher.class);

    assertThat(((MatcherBuilder) b.next("foo")).build(g)).isInstanceOf(TestMatcher.class);
    assertThat(((MatcherBuilder) b.next("foo", "bar")).build(g)).isInstanceOf(TestMatcher.class);

    assertThat(((MatcherBuilder) b.nextNot("foo")).build(g)).isInstanceOf(TestNotMatcher.class);
    assertThat(((MatcherBuilder) b.nextNot("foo", "bar")).build(g)).isInstanceOf(TestNotMatcher.class);

    assertThat(((MatcherBuilder) b.nothing()).build(g)).isInstanceOf(NothingMatcher.class);
    assertThat(((MatcherBuilder) b.regexp("foo")).build(g)).isInstanceOf(PatternMatcher.class);
    assertThat(((MatcherBuilder) b.endOfInput()).build(g)).isInstanceOf(EndOfInputMatcher.class);
    assertThat(((MatcherBuilder) b.token(mock(TokenType.class), "foo")).build(g)).isInstanceOf(TokenMatcher.class);
    assertThat(((MatcherBuilder) b.commentTrivia("foo")).build(g)).isInstanceOf(TriviaMatcher.class);
    assertThat(((MatcherBuilder) b.skippedTrivia("foo")).build(g)).isInstanceOf(TriviaMatcher.class);
  }

}
