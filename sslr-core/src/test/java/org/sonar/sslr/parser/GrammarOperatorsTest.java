/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2019 SonarSource SA
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
package org.sonar.sslr.parser;

import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia.TriviaKind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GrammarOperatorsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);

    assertThat(GrammarOperators.sequence(e1)).isSameAs(e1);
    assertThat(GrammarOperators.sequence(e1, e2)).isInstanceOf(SequenceExpression.class);
    assertThat(GrammarOperators.sequence("foo")).isInstanceOf(StringExpression.class);
    assertThat(GrammarOperators.sequence('f')).isInstanceOf(StringExpression.class);

    assertThat(GrammarOperators.firstOf(e1)).isSameAs(e1);
    assertThat(GrammarOperators.firstOf(e1, e2)).isInstanceOf(FirstOfExpression.class);

    assertThat(GrammarOperators.optional(e1)).isInstanceOf(OptionalExpression.class);

    assertThat(GrammarOperators.oneOrMore(e1)).isInstanceOf(OneOrMoreExpression.class);

    assertThat(GrammarOperators.zeroOrMore(e1)).isInstanceOf(ZeroOrMoreExpression.class);

    assertThat(GrammarOperators.next(e1)).isInstanceOf(NextExpression.class);

    assertThat(GrammarOperators.nextNot(e1)).isInstanceOf(NextNotExpression.class);

    assertThat(GrammarOperators.regexp("foo")).isInstanceOf(PatternExpression.class);

    assertThat(GrammarOperators.endOfInput()).isInstanceOf(EndOfInputExpression.class);

    assertThat(GrammarOperators.nothing()).isInstanceOf(NothingExpression.class);
  }

  @Test
  public void test_token() {
    TokenType tokenType = mock(TokenType.class);
    ParsingExpression e = mock(ParsingExpression.class);
    Object result = GrammarOperators.token(tokenType, e);
    assertThat(result).isInstanceOf(TokenExpression.class);
    assertThat(((TokenExpression) result).getTokenType()).isSameAs(tokenType);
  }

  @Test
  public void test_commentTrivia() {
    ParsingExpression e = mock(ParsingExpression.class);
    Object result = GrammarOperators.commentTrivia(e);
    assertThat(result).isInstanceOf(TriviaExpression.class);
    assertThat(((TriviaExpression) result).getTriviaKind()).isEqualTo(TriviaKind.COMMENT);
  }

  @Test
  public void test_skippedTrivia() {
    ParsingExpression e = mock(ParsingExpression.class);
    Object result = GrammarOperators.skippedTrivia(e);
    assertThat(result).isInstanceOf(TriviaExpression.class);
    assertThat(((TriviaExpression) result).getTriviaKind()).isEqualTo(TriviaKind.SKIPPED_TEXT);
  }

  @Test
  public void illegal_argument() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("java.lang.Object");
    GrammarOperators.sequence(new Object());
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = GrammarOperators.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
