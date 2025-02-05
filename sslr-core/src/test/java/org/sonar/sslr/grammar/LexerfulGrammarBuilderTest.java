/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.grammar;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;
import org.sonar.sslr.internal.grammar.MutableGrammar;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;
import org.sonar.sslr.internal.vm.lexerful.AnyTokenExpression;
import org.sonar.sslr.internal.vm.lexerful.TillNewLineExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeClassExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypeExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenTypesExpression;
import org.sonar.sslr.internal.vm.lexerful.TokenValueExpression;
import org.sonar.sslr.internal.vm.lexerful.TokensBridgeExpression;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class LexerfulGrammarBuilderTest {

  @Test
  public void should_create_expressions() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    ParsingExpression e3 = mock(ParsingExpression.class);

    assertThat(b.convertToExpression(e1)).isSameAs(e1);
    assertThat(b.convertToExpression("")).isInstanceOf(TokenValueExpression.class);
    assertThat(b.convertToExpression(mock(TokenType.class))).isInstanceOf(TokenTypeExpression.class);
    assertThat(b.convertToExpression(Object.class)).isInstanceOf(TokenTypeClassExpression.class);

    assertThat(b.sequence(e1, e2)).isInstanceOf(SequenceExpression.class);
    assertThat(b.sequence(e1, e2, e3)).isInstanceOf(SequenceExpression.class);

    assertThat(b.firstOf(e1, e2)).isInstanceOf(FirstOfExpression.class);
    assertThat(b.firstOf(e1, e2, e3)).isInstanceOf(FirstOfExpression.class);

    assertThat(b.optional(e1)).isInstanceOf(OptionalExpression.class);
    assertThat(b.optional(e1, e2)).isInstanceOf(OptionalExpression.class);

    assertThat(b.oneOrMore(e1)).isInstanceOf(OneOrMoreExpression.class);
    assertThat(b.oneOrMore(e1, e2)).isInstanceOf(OneOrMoreExpression.class);

    assertThat(b.zeroOrMore(e1)).isInstanceOf(ZeroOrMoreExpression.class);
    assertThat(b.zeroOrMore(e1, e2)).isInstanceOf(ZeroOrMoreExpression.class);

    assertThat(b.next(e1)).isInstanceOf(NextExpression.class);
    assertThat(b.next(e1, e2)).isInstanceOf(NextExpression.class);

    assertThat(b.nextNot(e1)).isInstanceOf(NextNotExpression.class);
    assertThat(b.nextNot(e1, e2)).isInstanceOf(NextNotExpression.class);

    assertThat(b.nothing()).as("singleton").isSameAs(NothingExpression.INSTANCE);

    assertThat(b.isOneOfThem(mock(TokenType.class), mock(TokenType.class))).isInstanceOf(TokenTypesExpression.class);
    assertThat(b.bridge(mock(TokenType.class), mock(TokenType.class))).isInstanceOf(TokensBridgeExpression.class);

    assertThat(b.adjacent(e1).toString()).isEqualTo("Sequence[Adjacent, " + e1 + "]");

    assertThat(b.anyTokenButNot(e1).toString()).isEqualTo("Sequence[NextNot[" + e1 + "], AnyToken]");

    assertThat(b.till(e1).toString()).isEqualTo("Sequence[ZeroOrMore[Sequence[NextNot[" + e1 + "], AnyToken]], " + e1 + "]");

    assertThat(b.exclusiveTill(e1).toString()).isEqualTo("ZeroOrMore[Sequence[NextNot[" + e1 + "], AnyToken]]");
    assertThat(b.exclusiveTill(e1, e2).toString()).isEqualTo("ZeroOrMore[Sequence[NextNot[FirstOf[" + e1 + ", " + e2 + "]], AnyToken]]");

    assertThat(b.everything()).as("singleton").isSameAs(AnyTokenExpression.INSTANCE);
    assertThat(b.anyToken()).as("singleton").isSameAs(AnyTokenExpression.INSTANCE);
    assertThat(b.tillNewLine()).as("singleton").isSameAs(TillNewLineExpression.INSTANCE);
  }

  @Test
  public void should_set_root_rule() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is(b.nothing());
    b.setRootRule(ruleKey);
    MutableGrammar grammar = (MutableGrammar) b.build();
    assertThat(((CompilableGrammarRule) grammar.getRootRule()).getRuleKey()).isSameAs(ruleKey);
  }

  @Test
  public void should_build_with_memoization() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    Grammar grammar = b.buildWithMemoizationOfMatchesForAllRules();
    assertThat(((RuleDefinition) grammar.rule(ruleKey)).shouldMemoize()).isTrue();
  }

  @Test
  public void test_undefined_root_rule() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.setRootRule(ruleKey);
    GrammarException thrown = assertThrows(GrammarException.class,
      b::build);
    assertEquals("The rule '" + ruleKey + "' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void test_undefined_rule() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey);
    GrammarException thrown = assertThrows(GrammarException.class,
      b::build);
    assertEquals("The rule '" + ruleKey + "' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void test_used_undefined_rule() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    b.rule(ruleKey1).is(ruleKey2);
    GrammarException thrown = assertThrows(GrammarException.class,
      b::build);
    assertEquals("The rule '" + ruleKey2 + "' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void test_incorrect_type_of_parsing_expression() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> LexerfulGrammarBuilder.create().convertToExpression(new Object()));
    assertEquals("Incorrect type of parsing expression: class java.lang.Object", thrown.getMessage());
  }

  @Test
  public void test_null_parsing_expression() {
    NullPointerException thrown = assertThrows(NullPointerException.class,
      () -> LexerfulGrammarBuilder.create().convertToExpression(null));
    assertEquals("Parsing expression can't be null", thrown.getMessage());
  }

  @Test
  public void should_fail_to_redefine() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> b.rule(ruleKey).is("foo"));
    assertEquals("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

  @Test
  public void should_fail_to_redefine2() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo", "bar");
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> b.rule(ruleKey).is("foo"));
    assertEquals("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

}
