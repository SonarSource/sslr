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

public class OrMatcherTest {

  @Test
  public void ok() {
    assertThat(or(isFalse(), isTrue()), match("one"));
    assertThat(or(isTrue(), isFalse()), match("one"));
    assertThat(or(isFalse(), isFalse()), not(match("one")));
  }

  @Test
  public void testToString() {
    assertEquals(or("(").toString(), "\"(\"");
    assertEquals(or("(", ")").toString(), "or");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(or("a", "a") == or("a", "a"), is(true));
    assertThat(or("a", "a") == or("a", "b"), is(false));
    assertThat(or("a", "a") == longestOne("a", "a"), is(false));
  }

}
