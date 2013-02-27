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
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.grammar.MutableLexerlessGrammar;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.NextExpression;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.NothingExpression;
import org.sonar.sslr.internal.vm.OneOrMoreExpression;
import org.sonar.sslr.internal.vm.OptionalExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.internal.vm.TokenExpression;
import org.sonar.sslr.internal.vm.TriviaExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;

import java.util.regex.PatternSyntaxException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LexerlessGrammarBuilderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_create_expressions() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);
    ParsingExpression e3 = mock(ParsingExpression.class);

    assertThat(b.convertToExpression(e1)).isSameAs(e1);
    assertThat(b.convertToExpression("")).isInstanceOf(StringExpression.class);
    assertThat(b.convertToExpression('c')).isInstanceOf(StringExpression.class);

    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    assertThat(b.convertToExpression(ruleKey)).isInstanceOf(MutableParsingRule.class);
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

    assertThat(b.nothing()).as("singleton").isSameAs(NothingExpression.INSTANCE);

    assertThat(b.regexp("")).isInstanceOf(PatternExpression.class);

    assertThat(b.endOfInput()).as("singleton").isSameAs(EndOfInputExpression.INSTANCE);
  }

  @Test
  public void test_token() {
    TokenType tokenType = mock(TokenType.class);
    ParsingExpression e = mock(ParsingExpression.class);
    Object result = LexerlessGrammarBuilder.create().token(tokenType, e);
    assertThat(result).isInstanceOf(TokenExpression.class);
    assertThat(((TokenExpression) result).getTokenType()).isSameAs(tokenType);
  }

  @Test
  public void test_commentTrivia() {
    ParsingExpression e = mock(ParsingExpression.class);
    Object result = LexerlessGrammarBuilder.create().commentTrivia(e);
    assertThat(result).isInstanceOf(TriviaExpression.class);
    assertThat(((TriviaExpression) result).getTriviaKind()).isEqualTo(TriviaKind.COMMENT);
  }

  @Test
  public void test_skippedTrivia() {
    ParsingExpression e = mock(ParsingExpression.class);
    Object result = LexerlessGrammarBuilder.create().skippedTrivia(e);
    assertThat(result).isInstanceOf(TriviaExpression.class);
    assertThat(((TriviaExpression) result).getTriviaKind()).isEqualTo(TriviaKind.SKIPPED_TEXT);
  }

  @Test
  public void should_set_root_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is(b.nothing());
    b.setRootRule(ruleKey);
    MutableLexerlessGrammar grammar = (MutableLexerlessGrammar) b.build();
    assertThat(grammar.getRootRuleKey()).isSameAs(ruleKey);
  }

  @Test
  public void should_build_based_on_another_builder() {
    LexerlessGrammarBuilder base = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    base.rule(ruleKey).is(base.nothing());
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.createBasedOn(base);
    assertThat(b.build().rule(ruleKey)).isNotNull();
  }

  @Test
  public void test_undefined_root_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.setRootRule(ruleKey);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' hasn't beed defined.");
    b.build();
  }

  @Test
  public void test_undefined_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' hasn't beed defined.");
    b.build();
  }

  @Test
  public void test_used_undefined_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    b.rule(ruleKey1).is(ruleKey2);
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey2 + "' hasn't beed defined.");
    b.build();
  }

  @Test
  public void test_wrong_regexp() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    thrown.expect(PatternSyntaxException.class);
    b.regexp("[");
  }

  @Test
  public void test_incorrect_type_of_parsing_expression() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Incorrect type of parsing expression: class java.lang.Object");
    LexerlessGrammarBuilder.create().convertToExpression(new Object());
  }

  @Test
  public void test_null_parsing_expression() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("Parsing expression can't be null");
    LexerlessGrammarBuilder.create().convertToExpression(null);
  }

  @Test
  public void should_fail_to_redefine() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    b.rule(ruleKey).is("foo");
  }

  @Test
  public void should_fail_to_redefine2() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo", "bar");
    thrown.expect(GrammarException.class);
    thrown.expectMessage("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    b.rule(ruleKey).is("foo", "bar");
  }

  @Test
  public void recovery_rule_not_supported() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    thrown.expect(UnsupportedOperationException.class);
    b.rule(ruleKey).recoveryRule();
  }

}
