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
package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class DelegatingMatcherTest {

  @Test
  public void test_unwrap() {
    Matcher delegate = MockedMatchers.mockTrue();
    Matcher matcher = newDelegatingMatcher(delegate);
    assertThat(DelegatingMatcher.unwrap(matcher)).isSameAs(delegate);
    assertThat(DelegatingMatcher.unwrap(delegate)).isSameAs(delegate);
  }

  @Test
  public void test_toString() {
    Matcher delegate = MockedMatchers.mockTrue();
    assertThat(newDelegatingMatcher(delegate).toString()).isEqualTo(delegate.toString());
  }

  @Test
  public void test_equals_and_hashCode() {
    Matcher delegate = MockedMatchers.mockTrue();
    Matcher first = newDelegatingMatcher(delegate);
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same delegate
    Matcher second = newDelegatingMatcher(delegate);
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different delegate
    Matcher third = newDelegatingMatcher(MockedMatchers.mockFalse());
    assertThat(first.equals(third)).isFalse();
  }

  private static DelegatingMatcher newDelegatingMatcher(Matcher delegate) {
    return new DelegatingMatcher(delegate) {
    };
  }

}
