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

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class NotMatcherTest {

  @Test
  public void ok() {
    assertThat(and("one", com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("two"), "three"), match("one three"));
    assertThat(and("one", com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("two"), "two"),
        org.hamcrest.Matchers.not(match("one two")));

    assertThat(and(opt(com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("one", "two")), "one"), opt(and("one", "two"))),
        match("one"));
    assertThat(and(opt(com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("one", "two")), "one"), opt(and("one", "two"))),
        match("one two"));
  }

  @Test
  public void testToString() {
    assertEquals(GrammarFunctions.Predicate.not("(").toString(), "not");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(
        com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("a") == com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("a"),
        is(true));
    assertThat(
        com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("a") == com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("b"),
        is(false));
    assertThat(com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("a") == next("a"), is(false));
  }

}
