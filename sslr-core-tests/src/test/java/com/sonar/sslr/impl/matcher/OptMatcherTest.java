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

public class OptMatcherTest {

  @Test
  public void ok() {
    assertThat(opt(isFalse()), not(match("one")));
    assertThat(opt(isTrue()), match("one"));
  }
  
  @Test
  public void testToString() {
  	assertEquals(opt("(").toString(), "opt");
  }
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(opt("a", "a") == opt("a", "a"), is(true));
  	assertThat(opt("a", "a") == opt("a", "b"), is(false));
  	assertThat(opt("a", "a") == longestOne("a", "a"), is(false));
  }

}
