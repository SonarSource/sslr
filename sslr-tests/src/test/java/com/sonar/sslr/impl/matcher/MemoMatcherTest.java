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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher.MatchResult;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MemoMatcherTest {

  @Test
  public void should_move_forward() {
    Matcher matcher = new MemoMatcher(MockedMatchers.mockTrue());
    ParsingState parsingState = mock(ParsingState.class);
    assertThat(matcher.isMatching(parsingState)).isTrue();
    assertThat(parsingState.lexerIndex).isEqualTo(0);
    matcher.match(parsingState);
    assertThat(parsingState.lexerIndex).isEqualTo(1);
  }

  @Test
  public void should_not_move_forward() {
    Matcher matcher = new MemoMatcher(MockedMatchers.mockFalse());
    ParsingState parsingState = mock(ParsingState.class);
    assertThat(matcher.isMatching(parsingState)).isFalse();
    assertThat(parsingState.lexerIndex).isEqualTo(0);
    try {
      matcher.match(parsingState);
      fail();
    } catch (BacktrackingEvent e) {
      // OK
    }
    assertThat(parsingState.lexerIndex).isEqualTo(0);
  }

  @Test
  public void should_return_memoized_result() {
    MemoizedMatcher matcher = new MemoMatcher(MockedMatchers.mockTrue());
    ParsingState parsingState = mock(ParsingState.class);
    AstNode astNode = mock(AstNode.class);
    when(astNode.getFromIndex()).thenReturn(1);
    when(astNode.getToIndex()).thenReturn(2);
    when(parsingState.getMemoizedAst(matcher)).thenReturn(astNode);

    AstNode result = matcher.match(parsingState);
    assertThat(parsingState.lexerIndex).isEqualTo(2);
    assertThat(result).isSameAs(astNode);
  }

  @Test
  public void should_memoize() {
    ParsingState parsingState = mock(ParsingState.class);
    AstNode astNode = mock(AstNode.class);
    MatchResult matchResult = MatchResult.succeed(parsingState, 1, astNode);
    MemoizedMatcher delegate = mock(MemoizedMatcher.class);
    when(delegate.doMatch(parsingState)).thenReturn(matchResult);
    MemoizedMatcher matcher = new MemoMatcher(delegate);

    assertThat(matcher.doMatch(parsingState)).isSameAs(matchResult);
    verify(parsingState).memoizeAst(matcher, astNode);
  }

  @Test
  public void test_toString() {
    Matcher delegate = MockedMatchers.mockTrue();
    assertThat(new MemoMatcher(delegate).toString()).isEqualTo(delegate.toString());
  }

  @Test
  public void test_equals_and_hashCode() {
    Matcher first = new MemoMatcher(MockedMatchers.mockTrue());
    assertThat(first.equals(first)).isTrue();
    assertThat(first.equals(null)).isFalse();
    // different matcher
    assertThat(first.equals(MockedMatchers.mockTrue())).isFalse();
    // same submatchers
    Matcher second = new MemoMatcher(MockedMatchers.mockTrue());
    assertThat(first.equals(second)).isTrue();
    assertThat(first.hashCode() == second.hashCode()).isTrue();
    // different submatchers
    Matcher third = new MemoMatcher(MockedMatchers.mockFalse());
    assertThat(first.equals(third)).isFalse();
  }

}
