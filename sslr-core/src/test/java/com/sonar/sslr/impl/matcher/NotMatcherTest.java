/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class NotMatcherTest {

  @Test
  public void ok() {
    assertThat(and("one", GrammarFunctions.Predicate.not("two"), "three"), match("one three"));
    assertThat(and("one", GrammarFunctions.Predicate.not("two"), "two"), not(match("one two")));
  }

  @Test
  public void testToString() {
    assertEquals(GrammarFunctions.Predicate.not("(").toString(), "not");
  }

}
