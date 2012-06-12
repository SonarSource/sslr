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

import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class AndMatcherTest {

  @Test
  public void should_move_forward() {
    Matcher matcher = new AndMatcher(MockedMatchers.mockTrue(), MockedMatchers.mockTrue());
    ParsingState parsingState = mock(ParsingState.class);
    assertThat(matcher.isMatching(parsingState)).isTrue();
    assertThat(parsingState.lexerIndex).isEqualTo(0);
    matcher.match(parsingState);
    assertThat(parsingState.lexerIndex).isEqualTo(2);
  }

  @Test
  public void should_not_move_forward() {
    Matcher matcher = new AndMatcher(MockedMatchers.mockTrue(), MockedMatchers.mockFalse());
    ParsingState parsingState = mock(ParsingState.class);
    assertThat(matcher.isMatching(parsingState)).isFalse();
    assertThat(parsingState.lexerIndex).isEqualTo(0);
    try {
      matcher.match(parsingState);
      fail();
    } catch (BacktrackingEvent e) {
      // OK
    }
    assertThat(parsingState.lexerIndex).isEqualTo(1);
  }

  @Test
  public void test_toString() {
    assertThat(new AndMatcher().toString()).isEqualTo("and");
  }

  @Test
  public void test_equals_and_hashCode() {
    Matcher first = new AndMatcher(MockedMatchers.mockTrue());
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same submatchers
    Matcher second = new AndMatcher(MockedMatchers.mockTrue());
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different submatchers
    Matcher third = new AndMatcher(MockedMatchers.mockFalse());
    assertThat(first.equals(third)).isFalse();
  }

}
