/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
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
