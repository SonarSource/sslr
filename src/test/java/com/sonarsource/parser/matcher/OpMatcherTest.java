/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import static com.sonarsource.parser.matcher.Matchers.isFalse;
import static com.sonarsource.parser.matcher.Matchers.isTrue;

import static org.junit.Assert.assertEquals;

public class OpMatcherTest extends MatcherCase {

  @Test
  public void testMany() {
    assertNotMatch(Matchers.opt(isFalse()), "one");
    assertMatch(Matchers.opt(isTrue()), "one");
  }

  @Test
  public void testToString() {
    assertEquals("(implements)?", Matchers.opt("implements").toString());
  }
}
