/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
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
