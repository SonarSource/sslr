/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import static com.sonarsource.parser.matcher.Matchers.isFalse;
import static com.sonarsource.parser.matcher.Matchers.isTrue;
import static com.sonarsource.parser.matcher.Matchers.o2n;

import static org.junit.Assert.assertEquals;

public class ZeroToNMatcherTest extends MatcherCase {

  @Test
  public void testMany() {
    assertNotMatch(o2n(isFalse()), "one");
    assertMatch(o2n(isTrue()), "one", "two", "three");
  }

  @Test
  public void testToString() {
    assertEquals("(public void class)*", o2n("public", "void", "class").toString());
  }
}
