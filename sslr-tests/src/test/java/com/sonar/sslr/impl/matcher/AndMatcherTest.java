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

public class AndMatcherTest {

  @Test
  public void ok() {
    assertThat(and(isTrue(), isTrue(), isTrue()), match("one two three"));
    assertThat(and(isTrue(), isFalse()), not(match("one two")));
    assertThat(and(isFalse(), isFalse()), not(match("one two")));
  }

  @Test
  public void testToString() {
    assertEquals(and("(").toString(), "\"(\"");
    assertEquals(and("(", ")").toString(), "and");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(and("a", "a") == and("a", "a"), is(true));
    assertThat(and("a", "a") == and("a", "b"), is(false));
    assertThat(and("a", "a") == longestOne("a", "a"), is(false));
  }

}
