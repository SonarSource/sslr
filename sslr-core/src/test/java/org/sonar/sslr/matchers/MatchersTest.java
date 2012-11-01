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
package org.sonar.sslr.matchers;

import com.sonar.sslr.api.TokenType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.matchers.*;

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MatchersTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test() {
    Matcher subMatcher = mock(Matcher.class);
    assertThat(Matchers.sequence(subMatcher)).isSameAs(subMatcher);
    assertThat(Matchers.sequence(subMatcher, subMatcher)).isInstanceOf(SequenceMatcher.class);
    assertThat(Matchers.sequence("foo")).isInstanceOf(StringMatcher.class);
    assertThat(Matchers.sequence('f')).isInstanceOf(StringMatcher.class);

    assertThat(Matchers.firstOf(subMatcher)).isSameAs(subMatcher);
    assertThat(Matchers.firstOf(subMatcher, subMatcher)).isInstanceOf(FirstOfMatcher.class);

    assertThat(Matchers.optional(subMatcher)).isInstanceOf(OptionalMatcher.class);

    assertThat(Matchers.oneOrMore(subMatcher)).isInstanceOf(OneOrMoreMatcher.class);

    assertThat(Matchers.zeroOrMore(subMatcher)).isInstanceOf(ZeroOrMoreMatcher.class);

    assertThat(Matchers.next(subMatcher)).isInstanceOf(TestMatcher.class);

    assertThat(Matchers.nextNot(subMatcher)).isInstanceOf(TestNotMatcher.class);

    assertThat(Matchers.regexp("foo")).isInstanceOf(PatternMatcher.class);

    assertThat(Matchers.endOfInput()).isInstanceOf(EndOfInputMatcher.class);

    assertThat(Matchers.nothing()).isInstanceOf(NothingMatcher.class);

    assertThat(Matchers.token(mock(TokenType.class), subMatcher)).isInstanceOf(TokenMatcher.class);
  }

  @Test
  public void illegal_argument() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("java.lang.Object");
    Matchers.sequence(new Object());
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = Matchers.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
