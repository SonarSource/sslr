/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.and;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NotMatcherTest {

  @Test
  public void ok() {
    assertThat(and("one", Matchers.not("two"), "three"), match("one three"));
    assertThat(and("one", Matchers.not("two"), "two"), not(match("one two")));
  }

  @Test
  public void testToString() {
    assertEquals("(implements)!", Matchers.not("implements").toString());
  }
}
