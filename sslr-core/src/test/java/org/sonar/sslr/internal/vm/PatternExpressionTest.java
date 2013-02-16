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
package org.sonar.sslr.internal.vm;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.sslr.internal.matchers.MatcherContext;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PatternExpressionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private PatternExpression expression = new PatternExpression("foo|bar");

  @Test
  public void should_compile() {
    assertThat(expression.compile()).containsOnly(expression);
    assertThat(expression.toString()).isEqualTo("Pattern foo|bar");
  }

  // TODO not a unit test
  @Test
  public void test() {
    Instruction[] instructions = expression.compile();
    assertThat(Machine.execute("foo", instructions)).isTrue();
    assertThat(Machine.execute("bar", instructions)).isTrue();
    assertThat(Machine.execute("baz", instructions)).isFalse();
  }

  @Test
  public void should_implement_Matcher() {
    thrown.expect(UnsupportedOperationException.class);
    expression.match(mock(MatcherContext.class));
  }

}
