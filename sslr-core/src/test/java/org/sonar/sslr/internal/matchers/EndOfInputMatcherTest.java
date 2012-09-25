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
import static org.mockito.Mockito.when;

public class EndOfInputMatcherTest {

  private MatcherContext context;
  private EndOfInputMatcher matcher;

  @Before
  public void setUp() {
    matcher = new EndOfInputMatcher();
    context = mock(MatcherContext.class);
  }

  @Test
  public void should_match() {
    when(context.length()).thenReturn(0);
    assertThat(matcher.match(context)).isTrue();
  }

  @Test
  public void should_not_match() {
    when(context.length()).thenReturn(1);
    assertThat(matcher.match(context)).isFalse();
  }

}
