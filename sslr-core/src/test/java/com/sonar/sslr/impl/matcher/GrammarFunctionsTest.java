/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.TokenType;
import org.junit.Test;
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

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class GrammarFunctionsTest {

  @Test
  public void test() {
    ParsingExpression e1 = mock(ParsingExpression.class);
    ParsingExpression e2 = mock(ParsingExpression.class);

    RuleDefinition rule = mock(RuleDefinition.class);
    assertThat(GrammarFunctions.Standard.and(rule)).isSameAs(rule);
    assertThat(GrammarFunctions.Standard.and(e1)).isSameAs(e1);
    assertThat(GrammarFunctions.Standard.and(e1, e2)).isInstanceOf(SequenceExpression.class);
    assertThat(GrammarFunctions.Standard.and("foo")).isInstanceOf(TokenValueExpression.class);
    assertThat(GrammarFunctions.Standard.and(mock(TokenType.class))).isInstanceOf(TokenTypeExpression.class);
    assertThat(GrammarFunctions.Standard.and(Object.class)).isInstanceOf(TokenTypeClassExpression.class);

    assertThat(GrammarFunctions.Standard.firstOf(e1)).isSameAs(e1);
    assertThat(GrammarFunctions.Standard.firstOf(e1, e2)).isInstanceOf(FirstOfExpression.class);

    assertThat(GrammarFunctions.Standard.or(e1)).isSameAs(e1);
    assertThat(GrammarFunctions.Standard.or(e1, e2)).isInstanceOf(FirstOfExpression.class);

    assertThat(GrammarFunctions.Standard.opt(e1)).isInstanceOf(OptionalExpression.class);

    assertThat(GrammarFunctions.Standard.o2n(e1)).isInstanceOf(ZeroOrMoreExpression.class);

    assertThat(GrammarFunctions.Standard.one2n(e1)).isInstanceOf(OneOrMoreExpression.class);

    assertThat(GrammarFunctions.Predicate.next(e1)).isInstanceOf(NextExpression.class);

    assertThat(GrammarFunctions.Predicate.not(e1)).isInstanceOf(NextNotExpression.class);

    assertThat(GrammarFunctions.Advanced.isTrue()).as("singleton").isSameAs(AnyTokenExpression.INSTANCE);

    assertThat(GrammarFunctions.Advanced.isFalse()).as("singleton").isSameAs(NothingExpression.INSTANCE);

    assertThat(GrammarFunctions.Advanced.tillNewLine()).as("singleton").isSameAs(TillNewLineExpression.INSTANCE);

    assertThat(GrammarFunctions.Advanced.bridge(mock(TokenType.class), mock(TokenType.class))).isInstanceOf(TokensBridgeExpression.class);

    assertThat(GrammarFunctions.Advanced.isOneOfThem(mock(TokenType.class), mock(TokenType.class))).isInstanceOf(TokenTypesExpression.class);

    assertThat(GrammarFunctions.Advanced.adjacent(e1).toString()).isEqualTo("Sequence[Adjacent, " + e1 + "]");

    assertThat(GrammarFunctions.Advanced.anyTokenButNot(e1).toString()).isEqualTo("Sequence[NextNot[" + e1 + "], AnyToken]");

    assertThat(GrammarFunctions.Advanced.till(e1).toString()).isEqualTo("Sequence[ZeroOrMore[Sequence[NextNot[" + e1 + "], AnyToken]], " + e1 + "]");

    assertThat(GrammarFunctions.Advanced.exclusiveTill(e1).toString()).isEqualTo("ZeroOrMore[Sequence[NextNot[" + e1 + "], AnyToken]]");
    assertThat(GrammarFunctions.Advanced.exclusiveTill(e1, e2).toString()).isEqualTo("ZeroOrMore[Sequence[NextNot[FirstOf[" + e1 + ", " + e2 + "]], AnyToken]]");
  }

  @Test
  public void firstOf_requires_at_least_one_argument() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      GrammarFunctions.Standard::firstOf);
    assertEquals("You must define at least one matcher.", thrown.getMessage());
  }

  @Test
  public void and_requires_at_least_one_argument() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      GrammarFunctions.Standard::and);
    assertEquals("You must define at least one matcher.", thrown.getMessage());
  }

  @Test
  public void isOneOfThem_requires_at_least_one_argument() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      GrammarFunctions.Advanced::isOneOfThem);
    assertEquals("You must define at least one matcher.", thrown.getMessage());
  }

  @Test
  public void test_incorrect_type_of_parsing_expression() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
      () -> GrammarFunctions.Standard.and(new Object()));
    assertThat(thrown.getMessage())
      .startsWith("The matcher object can't be anything else than a Rule, Matcher, String, TokenType or Class. Object = java.lang.Object@");
  }

  @Test
  public void private_constructors() throws Exception {
    assertThat(hasPrivateConstructor(GrammarFunctions.class)).isTrue();
    assertThat(hasPrivateConstructor(GrammarFunctions.Standard.class)).isTrue();
    assertThat(hasPrivateConstructor(GrammarFunctions.Predicate.class)).isTrue();
    assertThat(hasPrivateConstructor(GrammarFunctions.Advanced.class)).isTrue();
  }

  private static final boolean hasPrivateConstructor(Class cls) throws Exception {
    Constructor constructor = cls.getDeclaredConstructor();
    boolean result = !constructor.isAccessible();
    constructor.setAccessible(true);
    constructor.newInstance();
    return result;
  }

}
