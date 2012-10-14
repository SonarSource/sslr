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

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MemoizerTest {

  private MatcherContext context;
  private Memoizer memoizer;

  @Before
  public void setUp() {
    context = mock(MatcherContext.class);
    memoizer = new Memoizer(1);
  }

  @Test
  public void should_match() {
    Matcher matcher = mock(Matcher.class);
    ParseNode parseNode = mock(ParseNode.class);
    when(parseNode.getEndIndex()).thenReturn(42);
    when(parseNode.getMatcher()).thenReturn(matcher);
    when(context.getMatcher()).thenReturn(matcher);
    when(context.getNode()).thenReturn(parseNode);
    memoizer.onMatch(context);
    assertThat(memoizer.match(context)).isTrue();
    verify(context).createNode(parseNode);
    verify(context).setIndex(42);
  }

  @Test
  public void should_not_match_because_different_matcher() {
    Matcher matcher = mock(Matcher.class);
    ParseNode parseNode = mock(ParseNode.class);
    when(parseNode.getMatcher()).thenReturn(matcher);
    when(context.getMatcher()).thenReturn(matcher);
    when(context.getNode()).thenReturn(parseNode);
    memoizer.onMatch(context);

    Matcher anotherMatcher = mock(Matcher.class);
    when(context.getMatcher()).thenReturn(anotherMatcher);
    assertThat(memoizer.match(context)).isFalse();
  }

  @Test
  public void should_not_match_because_not_memoized() {
    Matcher matcher = mock(Matcher.class);
    ParseNode parseNode = mock(ParseNode.class);
    when(parseNode.getMatcher()).thenReturn(matcher);
    when(context.getMatcher()).thenReturn(matcher);
    when(context.getNode()).thenReturn(parseNode);
    memoizer.onMatch(context);

    when(context.getCurrentIndex()).thenReturn(1);
    assertThat(memoizer.match(context)).isFalse();
  }

  @Test
  public void no_operation_on_missmatch() {
    memoizer.onMissmatch(context);
    verifyZeroInteractions(context);
  }

}
