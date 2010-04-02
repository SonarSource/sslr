/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import org.junit.Test;

import static com.sonarsource.sslr.impl.matcher.Matchers.till;

import static org.junit.Assert.assertEquals;

public class InclusiveTillMatcherTest extends MatcherCase {

  @Test
  public void ok() {
    assertMatch(till("four"), "one", "two", "three", "four");
    assertNotMatch(till("three"), "one", "two", "three", "four");
  }

  @Test
  public void testToString() {
    assertEquals("(public)till", till("public").toString());
  }
}
