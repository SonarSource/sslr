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
import static org.fest.assertions.Fail.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatcherContextTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Matcher matcher;
  private MatcherContext context;

  @Before
  public void setUp() {
    matcher = mock(Matcher.class);
    context = new BasicMatcherContext(new ImmutableInputBuffer("bar".toCharArray()), mock(MatchHandler.class), matcher);
  }

  @Test
  public void should_reset_index() {
    assertThat(context.getCurrentIndex()).isEqualTo(0);
    context.advanceIndex(2);
    assertThat(context.getCurrentIndex()).isEqualTo(2);
    context.resetIndex();
    assertThat(context.getCurrentIndex()).isEqualTo(0);
  }

  @Test
  public void should_set_index() {
    assertThat(context.getCurrentIndex()).isEqualTo(0);
    context.setIndex(42);
    assertThat(context.getCurrentIndex()).isEqualTo(42);
  }

  @Test
  public void should_return_remaining_length() {
    assertThat(context.length()).isEqualTo(3);
    context.advanceIndex(2);
    assertThat(context.length()).isEqualTo(1);
  }

  @Test
  public void should_return_charAt() {
    assertThat(context.charAt(0)).isEqualTo('b');
    assertThat(context.charAt(1)).isEqualTo('a');
    assertThat(context.charAt(2)).isEqualTo('r');
    context.advanceIndex(2);
    assertThat(context.charAt(0)).isEqualTo('r');
  }

  @Test
  public void incorrect_index_for_charAt() {
    thrown.expect(IndexOutOfBoundsException.class);
    context.charAt(3);
  }

  @Test
  public void subSequence_unsupported() {
    thrown.expect(UnsupportedOperationException.class);
    context.subSequence(0, 1);
  }

  @Test
  public void should_provide_subContext() {
    Matcher subMatcher = mock(Matcher.class);
    context.advanceIndex(1);
    MatcherContext subContext = context.getSubContext(subMatcher);
    assertThat(context.getSubContext(subMatcher)).isSameAs(subContext);
    context.resetIndex();
    assertThat(context.getCurrentIndex()).isEqualTo(0);
    assertThat(subContext.getCurrentIndex()).isEqualTo(1);
    subContext.advanceIndex(1);
    assertThat(context.getCurrentIndex()).isEqualTo(0);
    assertThat(subContext.getCurrentIndex()).isEqualTo(2);
    subContext.resetIndex();
    assertThat(context.getCurrentIndex()).isEqualTo(0);
    assertThat(subContext.getCurrentIndex()).isEqualTo(1);
  }

  @Test
  public void should_wrap_Exception() {
    Matcher subMatcher = mock(Matcher.class);
    MatcherContext subContext = context.getSubContext(subMatcher);
    RuntimeException cause = new RuntimeException();
    when(subMatcher.match(subContext)).thenThrow(cause);
    try {
      subContext.runMatcher();
      fail();
    } catch (ParserRuntimeException e) {
      assertThat(e.getCause()).isSameAs(cause);
    }
  }

  @Test
  public void should_propagate_ParserRuntimeException() {
    ParserRuntimeException exception = new ParserRuntimeException(null);
    when(matcher.match(context)).thenThrow(exception);
    try {
      context.runMatcher();
    } catch (ParserRuntimeException e) {
      assertThat(e).isSameAs(exception);
    }
  }

}
