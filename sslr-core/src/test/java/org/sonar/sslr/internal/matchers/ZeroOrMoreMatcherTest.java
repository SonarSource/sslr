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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ZeroOrMoreMatcherTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Matcher subMatcher;
  private MatcherContext context, subContext;
  private ZeroOrMoreMatcher matcher;

  @Before
  public void setUp() {
    subMatcher = mock(Matcher.class);
    subContext = mock(MatcherContext.class);
    matcher = new ZeroOrMoreMatcher(subMatcher);
    context = mock(MatcherContext.class);
    when(context.getSubContext(subMatcher)).thenReturn(subContext);
  }

  @Test
  public void should_match() {
    when(subContext.runMatcher()).thenReturn(true, true, false);
    when(context.getCurrentIndex()).thenReturn(2, 3, 4);
    assertThat(matcher.match(context)).isTrue();
    verify(subContext, times(3)).runMatcher();
  }

  @Test
  public void should_match_empty() {
    when(subContext.runMatcher()).thenReturn(false);
    assertThat(matcher.match(context)).isTrue();
    verify(subContext).runMatcher();
  }

  @Test
  public void should_check_that_moves_forward() {
    when(subContext.runMatcher()).thenReturn(true, true, false);
    when(context.getCurrentIndex()).thenReturn(1, 1, 1);
    thrown.expect(GrammarException.class);
    matcher.match(context);
  }

}
