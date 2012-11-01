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
package org.sonar.sslr.internal.matchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchersUtilsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_convert_to_matcher() {
    assertThat(MatchersUtils.convertToMatcher("foo")).isInstanceOf(StringMatcher.class);
    assertThat(MatchersUtils.convertToMatcher('f')).isInstanceOf(StringMatcher.class);

    Matcher matcher = mock(Matcher.class);
    assertThat(MatchersUtils.convertToMatcher(matcher)).isSameAs(matcher);
  }

  @Test
  public void should_convert_to_matchers() {
    Matcher matcher1 = mock(Matcher.class);
    Matcher matcher2 = mock(Matcher.class);
    assertThat(MatchersUtils.convertToMatchers(matcher1)).containsOnly(matcher1);

    Object[] subMatchers = new Matcher[] {matcher1, matcher2};
    assertThat(MatchersUtils.convertToMatchers(subMatchers)).isEqualTo(subMatchers);

    SequenceMatcher sequenceMatcher = when(mock(SequenceMatcher.class).getSubMatchers())
        .thenReturn(new Matcher[] {matcher1, matcher2})
        .getMock();
    assertThat(MatchersUtils.convertToMatchers(sequenceMatcher)).isEqualTo(subMatchers);
  }

  @Test
  public void should_convert_to_single_matcher() {
    Matcher matcher1 = mock(Matcher.class);
    Matcher matcher2 = mock(Matcher.class);
    assertThat(MatchersUtils.convertToSingleMatcher(matcher1)).isSameAs(matcher1);
    assertThat(MatchersUtils.convertToSingleMatcher(matcher1, matcher2)).isInstanceOf(SequenceMatcher.class);
  }

  @Test
  public void illegal_argument() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("java.lang.Object");
    MatchersUtils.convertToMatcher(new Object());
  }

  @Test
  public void private_constructor() throws Exception {
    Constructor constructor = MatchersUtils.class.getDeclaredConstructor();
    assertThat(constructor.isAccessible()).isFalse();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
