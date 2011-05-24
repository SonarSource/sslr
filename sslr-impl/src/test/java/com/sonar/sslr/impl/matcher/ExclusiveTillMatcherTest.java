/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.adjacent;
import static com.sonar.sslr.impl.matcher.Matchers.and;
import static com.sonar.sslr.impl.matcher.Matchers.exclusiveTill;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExclusiveTillMatcherTest {

  @Test
  public void ok() {
    assertThat(and(exclusiveTill("four"), "four"), match("one two three four"));
    assertThat(and(exclusiveTill("two", "three"), "two", "three", "four"), match("one two three four"));
  }
  
  @Test
  public void testToString() {
  	assertEquals(exclusiveTill("(").toString(), "exclusiveTill");
  }

}
