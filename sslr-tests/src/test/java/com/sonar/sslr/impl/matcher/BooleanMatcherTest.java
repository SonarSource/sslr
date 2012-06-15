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

import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.test.lexer.MockHelper;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BooleanMatcherTest {

  @Test
  public void ok() {
    Token token = MockHelper.mockToken(mock(TokenType.class), "foo");
    assertThat(new BooleanMatcher(true).isExpectedToken(token)).isTrue();
    assertThat(new BooleanMatcher(false).isExpectedToken(token)).isFalse();
  }

  @Test
  public void test_toString() {
    assertThat(new BooleanMatcher(true).toString()).isEqualTo("isTrue()");
    assertThat(new BooleanMatcher(false).toString()).isEqualTo("isFalse()");
  }

  @Test
  public void test_equals_and_hashCode() {
    Matcher first = new BooleanMatcher(true);
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same internal state
    Matcher second = new BooleanMatcher(true);
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different internal state
    Matcher third = new BooleanMatcher(false);
    assertThat(first.equals(third)).isFalse();
  }

}
