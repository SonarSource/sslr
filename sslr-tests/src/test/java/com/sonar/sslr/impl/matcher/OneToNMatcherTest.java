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

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OneToNMatcherTest {

  @Test
  public void ok() {
    assertThat(one2n(isTrue()), match("one"));
    assertThat(one2n(isFalse()), not(match("one")));
  }

  @Test
  public void testToString() {
    assertEquals(one2n("(").toString(), "one2n");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(one2n("a", "a") == one2n("a", "a"), is(true));
    assertThat(one2n("a", "a") == one2n("a", "b"), is(false));
    assertThat(one2n("a", "a") == longestOne("a", "a"), is(false));
  }

}
