/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotMatcherTest extends MatcherCase {

  @Test
  public void testMany() {
    assertMatch(Matchers.not("two"), "one");
    assertNotMatch(Matchers.not("one"), "one");
  }

  @Test
  public void testToString() {
    assertEquals("(implements)!", Matchers.not("implements").toString());
  }
}
