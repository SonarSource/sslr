/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.adjacent;
import static com.sonar.sslr.impl.matcher.Matchers.till;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InclusiveTillMatcherTest {

  @Test
  public void ok() {
    assertThat(till("four"), match("one two three four"));
    assertThat(till("three"), not(match("one two three four")));
  }
  
  @Test
  public void testToString() {
  	assertEquals(till("(").toString(), "till");
  }

}
