/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NextMatcherTest {

  @Test
  public void ok() {
    assertThat(and(next("one"), "one"), match("one"));
    assertThat(and(next("two"), "one"), not(match("one")));
  }
  
  @Test
  public void testToString() {
  	assertEquals(next("(").toString(), "next");
  }
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(next("a", "a") == next("a", "a"), is(true));
  	assertThat(next("a", "a") == next("a", "b"), is(false));
  	assertThat(next("a", "a") == and("a", "a"), is(false));
  }

}
