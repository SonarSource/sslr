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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatternMatcherTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private MatcherContext context;
  private PatternMatcher matcher;

  @Before
  public void setUp() {
    context = mock(MatcherContext.class);
    matcher = new PatternMatcher("a*b");
  }

  @Test
  public void should_match() {
    when(context.length()).thenReturn(3);
    when(context.charAt(0)).thenReturn('a');
    when(context.charAt(1)).thenReturn('a');
    when(context.charAt(2)).thenReturn('b');
    assertThat(matcher.match(context)).isTrue();
    verify(context).advanceIndex(3);
    verify(context).createNode();
  }

  @Test
  public void should_not_match() {
    assertThat(matcher.match(context)).isFalse();
    verify(context, never()).advanceIndex(anyInt());
    verify(context, never()).createNode();
  }

}
