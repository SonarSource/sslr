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
package org.sonar.sslr.parser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.MatcherPathElement;

import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParseErrorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void constructor_should_fail_with_empty_failed_path() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("failedPaths must contain at least one element");
    new ParseError(mock(InputBuffer.class), 0, "", Collections.EMPTY_LIST);
  }

  @Test
  public void getFailedPaths() {
    List<List<MatcherPathElement>> failedPaths = mock(List.class);
    when(failedPaths.size()).thenReturn(1);
    assertThat(new ParseError(mock(InputBuffer.class), 0, "", failedPaths).getFailedPaths()).isEqualTo(failedPaths);
  }

}
