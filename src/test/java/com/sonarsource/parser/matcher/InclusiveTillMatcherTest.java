/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import static com.sonarsource.parser.matcher.Matchers.till;

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
