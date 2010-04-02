/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import org.junit.Test;

import static com.sonarsource.sslr.impl.matcher.Matchers.and;
import static com.sonarsource.sslr.impl.matcher.Matchers.exclusiveTill;

import static org.junit.Assert.assertEquals;

public class ExclusiveTillMatcherTest extends MatcherCase {

  @Test
  public void ok() {
    assertMatch(and(exclusiveTill("four"), "four"), "one", "two", "three", "four");
    assertMatch(and(exclusiveTill("two", "three"), "two", "three", "four"), "one", "two", "three", "four");
  }

  @Test
  public void testToString() {
    assertEquals("(public | private)exclusiveTill", exclusiveTill("public", "private").toString());
  }
}
