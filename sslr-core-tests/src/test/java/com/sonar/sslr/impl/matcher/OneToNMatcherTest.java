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
