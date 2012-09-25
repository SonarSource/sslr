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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirstOfMatcherTest {

  private Matcher subMatcher1, subMatcher2;
  private MatcherContext context, subContext1, subContext2;
  private FirstOfMatcher matcher;

  @Before
  public void setUp() {
    subMatcher1 = mock(Matcher.class);
    subMatcher2 = mock(Matcher.class);
    subContext1 = mock(MatcherContext.class);
    subContext2 = mock(MatcherContext.class);
    matcher = new FirstOfMatcher(subMatcher1, subMatcher2);
    context = mock(MatcherContext.class);
    when(context.getSubContext(subMatcher1)).thenReturn(subContext1);
    when(context.getSubContext(subMatcher2)).thenReturn(subContext2);
  }

  @Test
  public void first_should_match() {
    when(subContext1.runMatcher()).thenReturn(true);
    when(subContext2.runMatcher()).thenReturn(false);
    assertThat(matcher.match(context)).isTrue();
    verify(subContext1).runMatcher();
    verify(subContext2, never()).runMatcher();
  }

  @Test
  public void second_should_match() {
    when(subContext1.runMatcher()).thenReturn(false);
    when(subContext2.runMatcher()).thenReturn(true);
    assertThat(matcher.match(context)).isTrue();
    verify(subContext1).runMatcher();
    verify(subContext2).runMatcher();
  }

  @Test
  public void should_not_match() {
    when(subContext1.runMatcher()).thenReturn(false);
    when(subContext2.runMatcher()).thenReturn(false);
    assertThat(matcher.match(context)).isFalse();
    verify(subContext1).runMatcher();
    verify(subContext2).runMatcher();
  }

}
