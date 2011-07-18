/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AtLeastOneMatcherTest {

  @Test
  public void ok() {
    assertThat(atLeastOne(isTrue(), isTrue(), isTrue()), match("one two three"));
    assertThat(atLeastOne(isFalse(), isTrue(), isTrue()), match("two three"));
    assertThat(atLeastOne(isTrue(), isFalse()), match("one"));
    assertThat(atLeastOne(isFalse(), isTrue()), match("two"));
    assertThat(atLeastOne(isFalse(), isFalse()), not(match("one two")));
  }
  
  @Test
  public void testToString() {
  	assertEquals(atLeastOne("(", ")").toString(), "atLeastOne");
  }
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(atLeastOne("a", "b") == atLeastOne("a", "b"), is(true));
  	assertThat(atLeastOne("a", "b") == atLeastOne("a", "c"), is(false));
  	assertThat(atLeastOne("a", "a") == and("a", "b"), is(false));
  }

}
