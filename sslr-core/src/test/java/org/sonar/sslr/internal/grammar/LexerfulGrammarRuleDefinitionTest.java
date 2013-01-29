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
package org.sonar.sslr.internal.grammar;

import com.sonar.sslr.api.Grammar;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.sslr.grammar.GrammarRule;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LexerfulGrammarRuleDefinitionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private GrammarRule grammarRule;
  private LexerfulGrammarRuleDefinition definition;

  @Before
  public void init() {
    grammarRule = mock(GrammarRule.class);
    definition = new LexerfulGrammarRuleDefinition(grammarRule);
  }

  @Test
  public void should_return_rule_name() {
    assertThat(definition.getName()).isEqualTo(grammarRule.toString());
  }

  @Test
  public void should_return_rule() {
    assertThat(definition.getRule()).isSameAs(grammarRule);
  }

  @Test
  public void should_fail_to_build_if_not_defined() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule '" + definition.getName() + "' hasn't beed defined.");
    definition.build(mock(Grammar.class));
  }

  @Test
  public void should_fail_to_to_redefine() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The rule '" + definition.getName() + "' has already been defined somewhere in the grammar.");
    definition.is("foo");
    definition.is("foo");
  }

  @Test
  public void should_build_rule_with_definition() {
    definition.is("foo");

    com.sonar.sslr.api.Rule ruleMatcher = mock(com.sonar.sslr.api.Rule.class);
    Grammar g = mock(Grammar.class);
    when(g.rule(grammarRule)).thenReturn(ruleMatcher);

    definition.build(g);
    verify(ruleMatcher).is(Mockito.any());
  }

  @Test
  public void should_build_rule_with_overriden_definition() {
    MatcherBuilder matcherBuilder = mock(MatcherBuilder.class);

    definition.is("foo");
    definition.override(matcherBuilder);

    com.sonar.sslr.api.Rule ruleMatcher = mock(com.sonar.sslr.api.Rule.class);
    Grammar g = mock(Grammar.class);
    when(g.rule(grammarRule)).thenReturn(ruleMatcher);

    definition.build(g);
    verify(ruleMatcher).is(Mockito.any());
    verify(matcherBuilder).build(g);
  }

  @Test
  public void should_not_skip_nor_recovery_rule_by_default() {
    definition.is("foo");

    com.sonar.sslr.api.Rule ruleMatcher = mock(com.sonar.sslr.api.Rule.class);
    Grammar g = mock(Grammar.class);
    when(g.rule(grammarRule)).thenReturn(ruleMatcher);

    definition.build(g);
    verify(ruleMatcher, Mockito.never()).skip();
    verify(ruleMatcher, Mockito.never()).skipIfOneChild();
    verify(ruleMatcher, Mockito.never()).recoveryRule();
  }

  @Test
  public void should_skip() {
    definition.is("foo");
    definition.skip();

    com.sonar.sslr.api.Rule ruleMatcher = mock(com.sonar.sslr.api.Rule.class);
    Grammar g = mock(Grammar.class);
    when(g.rule(grammarRule)).thenReturn(ruleMatcher);

    definition.build(g);
    verify(ruleMatcher).skip();
  }

  @Test
  public void should_skip_if_one_child() {
    definition.is("foo");
    definition.skipIfOneChild();

    com.sonar.sslr.api.Rule ruleMatcher = mock(com.sonar.sslr.api.Rule.class);
    Grammar g = mock(Grammar.class);
    when(g.rule(grammarRule)).thenReturn(ruleMatcher);

    definition.build(g);
    verify(ruleMatcher).skipIfOneChild();
  }

  @Test
  public void should_recovery_rule() {
    definition.is("foo");
    definition.recoveryRule();

    com.sonar.sslr.api.Rule ruleMatcher = mock(com.sonar.sslr.api.Rule.class);
    Grammar g = mock(Grammar.class);
    when(g.rule(grammarRule)).thenReturn(ruleMatcher);

    definition.build(g);
    verify(ruleMatcher).recoveryRule();
  }

}
