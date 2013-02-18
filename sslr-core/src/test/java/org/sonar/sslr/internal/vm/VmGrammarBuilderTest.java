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
package org.sonar.sslr.internal.vm;

import com.sonar.sslr.api.TokenType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;

import java.util.regex.PatternSyntaxException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class VmGrammarBuilderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_create_expressions() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    ParsingExpression e3 = mock(ParsingExpression.class);

    assertThat(b.convertToExpression(e1)).isSameAs(e1);
    assertThat(b.convertToExpression("")).isInstanceOf(StringExpression.class);
    assertThat(b.convertToExpression('c')).isInstanceOf(StringExpression.class);

    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    assertThat(b.convertToExpression(ruleKey)).isInstanceOf(RuleRefExpression.class);
    assertThat(b.convertToExpression(ruleKey)).isSameAs(b.convertToExpression(ruleKey));

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

    assertThat(b.nothing()).isInstanceOf(NothingExpression.class);

    assertThat(b.regexp("")).isInstanceOf(PatternExpression.class);

    assertThat(b.endOfInput()).isInstanceOf(EndOfInputExpression.class);

    assertThat(b.commentTrivia(e1)).isInstanceOf(TriviaExpression.class);
    assertThat(b.skippedTrivia(e1)).isInstanceOf(TriviaExpression.class);

    assertThat(b.token(mock(TokenType.class), e1)).isSameAs(e1);
  }

  @Test
  public void should_set_root_rule() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is(b.nothing());
    b.setRootRule(ruleKey);
    CompiledGrammar grammar = b.build();
    assertThat(grammar.getRootRuleKey()).isSameAs(ruleKey);
  }

  @Test
  public void test_wrong_root_rule() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.setRootRule(ruleKey);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' hasn't beed defined.");
    b.build();
  }

  @Test
  public void test_wrong_argument() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Incorrect type of parsing expression: class java.lang.Object");
    VmGrammarBuilder.create().convertToExpression(new Object());
  }

  @Test
  public void test_undefined_rule() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' hasn't beed defined.");
    b.build();
  }

  @Test
  public void test_used_undefined_rule() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    b.rule(ruleKey1).is(ruleKey2);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule " + ruleKey2 + " has been used somewhere in grammar, but not defined.");
    b.build();
  }

  @Test
  public void test_wrong_regexp() {
    VmGrammarBuilder b = VmGrammarBuilder.create();
    thrown.expect(PatternSyntaxException.class);
    b.regexp("[");
  }

}
