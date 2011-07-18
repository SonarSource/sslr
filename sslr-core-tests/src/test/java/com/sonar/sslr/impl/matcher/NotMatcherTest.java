/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.adjacent;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.anyTokenButNot;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class NotMatcherTest {

  @Test
  public void ok() {
    assertThat(and("one", not("two"), "three"), match("one three"));
    assertThat(and("one", not("two"), "two"), org.hamcrest.Matchers.not(match("one two")));
    
    assertThat(and(opt(not(and("one", "two")), "one"), opt(and("one", "two"))), match("one"));
    assertThat(and(opt(not(and("one", "two")), "one"), opt(and("one", "two"))), match("one two"));
  }

  @Test
  public void testToString() {
    assertEquals(GrammarFunctions.Predicate.not("(").toString(), "not");
  }
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(not("a") == not("a"), is(true));
  	assertThat(not("a") == not("b"), is(false));
  	assertThat(not("a") == anyTokenButNot("a"), is(false));
  }

}
