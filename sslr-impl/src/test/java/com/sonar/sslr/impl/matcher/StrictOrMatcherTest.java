/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import static com.sonar.sslr.impl.matcher.Matchers.isFalse;
import static com.sonar.sslr.impl.matcher.Matchers.isTrue;
import static com.sonar.sslr.impl.matcher.Matchers.strictOr;

import static org.junit.Assert.assertEquals;

public class StrictOrMatcherTest extends MatcherCase {

  @Test
  public void testStrictOr() {
    assertMatch(strictOr(isFalse(), isTrue()), "one");
    assertMatch(strictOr(isTrue(), isFalse()), "one");
    assertNotMatch(strictOr(isFalse(), isFalse()), "one");
  }

  @Test(expected = IllegalStateException.class)
  public void testTwoWaysMatch() {
    assertMatch(strictOr(isTrue(), isTrue()), "one");
  }

  @Test
  public void testToString() {
    assertEquals("(extends | implements)", strictOr("extends", "implements").toString());
  }
}
