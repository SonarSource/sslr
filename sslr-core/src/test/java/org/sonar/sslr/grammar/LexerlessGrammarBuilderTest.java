/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.grammar;

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.junit.Test;
import org.sonar.sslr.internal.grammar.MutableGrammar;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class LexerlessGrammarBuilderTest {

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
    MutableGrammar grammar = (MutableGrammar) b.build();
    assertThat(((CompilableGrammarRule) grammar.getRootRule()).getRuleKey()).isSameAs(ruleKey);
  }

  @Test
  public void test_undefined_root_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.setRootRule(ruleKey);
    GrammarException thrown = assertThrows(GrammarException.class,
      b::build);
    assertEquals("The rule '" + ruleKey + "' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void test_undefined_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey);
    GrammarException thrown = assertThrows(GrammarException.class,
      b::build);
    assertEquals("The rule '" + ruleKey + "' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void test_used_undefined_rule() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey1 = mock(GrammarRuleKey.class);
    GrammarRuleKey ruleKey2 = mock(GrammarRuleKey.class);
    b.rule(ruleKey1).is(ruleKey2);
    GrammarException thrown = assertThrows(GrammarException.class,
      b::build);
    assertEquals("The rule '" + ruleKey2 + "' hasn't been defined.", thrown.getMessage());
  }

  @Test
  public void test_wrong_regexp() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    assertThrows(PatternSyntaxException.class,
      () -> b.regexp("["));
  }

  @Test
  public void test_incorrect_type_of_parsing_expression() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> LexerlessGrammarBuilder.create().convertToExpression(new Object()));
    assertEquals("Incorrect type of parsing expression: class java.lang.Object", thrown.getMessage());
  }

  @Test
  public void test_null_parsing_expression() {
    NullPointerException thrown = assertThrows(NullPointerException.class,
      () -> LexerlessGrammarBuilder.create().convertToExpression(null));
    assertEquals("Parsing expression can't be null", thrown.getMessage());
  }

  @Test
  public void should_fail_to_redefine() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo");
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> b.rule(ruleKey).is("foo"));
    assertEquals("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

  @Test
  public void should_fail_to_redefine2() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();
    GrammarRuleKey ruleKey = mock(GrammarRuleKey.class);
    b.rule(ruleKey).is("foo", "bar");
    GrammarException thrown = assertThrows(GrammarException.class,
      () -> b.rule(ruleKey).is("foo", "bar"));
    assertEquals("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.", thrown.getMessage());
  }

}
