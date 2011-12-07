/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

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
    assertThat(com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not("a") == anyTokenButNot("a"), is(false));
  }

}
