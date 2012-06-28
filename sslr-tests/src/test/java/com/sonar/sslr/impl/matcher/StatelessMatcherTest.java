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
import com.sonar.sslr.impl.ParsingState;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StatelessMatcherTest {

  @Test
  public void testEqualsAndHashCode() {
    Matcher child1 = mock(Matcher.class);
    Matcher child2 = mock(Matcher.class);
    StatelessMatcher matcher1 = newMatcher(child1);
    StatelessMatcher matcher2 = newMatcher(child1);
    StatelessMatcher matcher3 = newMatcher(child2);

    assertThat(matcher1.equals(matcher1)).isTrue();
    assertThat(matcher1.equals(matcher2)).isTrue();
    assertThat(matcher1.hashCode() == matcher2.hashCode()).isTrue();

    assertThat(matcher1.equals(null)).isFalse();
    assertThat(matcher1.equals(new Object())).isFalse();
    assertThat(matcher1.equals(matcher3)).isFalse();
  }

  private StatelessMatcher newMatcher(Matcher child) {
    return new StatelessMatcher(child) {
      @Override
      protected AstNode matchWorker(ParsingState parsingState) {
        return null;
      }
    };
  }

}
