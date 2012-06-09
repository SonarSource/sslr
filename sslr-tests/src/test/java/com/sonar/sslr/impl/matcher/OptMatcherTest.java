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

import org.junit.Test;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.isFalse;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.isTrue;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.longestOne;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class OptMatcherTest {

  @Test
  public void ok() {
    assertThat(opt(isFalse()), not(match("one")));
    assertThat(opt(isTrue()), match("one"));
  }

  @Test
  public void testToString() {
    assertThat(opt("(").toString()).isEqualTo("opt");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(opt("a", "a") == opt("a", "a")).isTrue();
    assertThat(opt("a", "a") == opt("a", "b")).isFalse();
    assertThat(opt("a", "a") == longestOne("a", "a")).isFalse();
  }

}
