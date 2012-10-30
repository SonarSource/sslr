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
import static org.mockito.Mockito.when;

public class ErrorLocatingHandlerTest {

  private MatchHandler delegate;
  private ErrorLocatingHandler errorLocatingHandler;

  @Before
  public void setUp() {
    delegate = mock(MatchHandler.class);
    errorLocatingHandler = new ErrorLocatingHandler(delegate);
  }

  @Test
  public void should_delegate_match() {
    MatcherContext context = mock(MatcherContext.class);
    errorLocatingHandler.match(context);
    verify(delegate).match(context);
  }

  @Test
  public void should_delegate_onMatch() {
    MatcherContext context = mock(MatcherContext.class);
    errorLocatingHandler.onMatch(context);
    verify(delegate).onMatch(context);
  }

  @Test
  public void should_delegate_onMissmatch() {
    MatcherContext context = mock(MatcherContext.class);
    errorLocatingHandler.onMissmatch(context);
    verify(delegate).onMissmatch(context);
  }

  @Test
  public void should_find_location_of_error() {
    MatcherContext context = mock(MatcherContext.class);
    when(context.getCurrentIndex()).thenReturn(1);
    errorLocatingHandler.onMissmatch(context);
    assertThat(errorLocatingHandler.getErrorIndex()).isEqualTo(1);
    when(context.getCurrentIndex()).thenReturn(3);
    errorLocatingHandler.onMissmatch(context);
    assertThat(errorLocatingHandler.getErrorIndex()).isEqualTo(3);
    when(context.getCurrentIndex()).thenReturn(2);
    errorLocatingHandler.onMissmatch(context);
    assertThat(errorLocatingHandler.getErrorIndex()).isEqualTo(3);
  }

}
