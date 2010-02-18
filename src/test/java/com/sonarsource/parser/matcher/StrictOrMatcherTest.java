/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.sonarsource.parser.matcher.Matchers.isFalse;
import static com.sonarsource.parser.matcher.Matchers.isTrue;
import static com.sonarsource.parser.matcher.Matchers.or;

import static org.junit.Assert.assertEquals;

@Ignore
public class StrictOrMatcherTest extends MatcherCase {

  @Before
  public void init() {
    // Matchers.strictMatchingMode = true;
  }

  @Test
  public void testStrictOr() {
    assertMatch(or(isFalse(), isTrue()), "one");
    assertMatch(or(isTrue(), isFalse()), "one");
    assertNotMatch(or(isFalse(), isFalse()), "one");
  }

  @Test(expected = IllegalStateException.class)
  public void testTwoWaysMatch() {
    assertMatch(or(isTrue(), isTrue()), "one");
  }

  @Test
  public void testToString() {
    assertEquals("(extends | implements)", or("extends", "implements").toString());
  }
}
