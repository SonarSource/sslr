/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import org.junit.Test;

import static com.sonarsource.parser.matcher.Matchers.isFalse;
import static com.sonarsource.parser.matcher.Matchers.isTrue;
import static com.sonarsource.parser.matcher.Matchers.or;

import static org.junit.Assert.assertEquals;

public class OrMatcherTest extends MatcherCase {

  @Test
  public void testOr() {
    assertMatch(or(isFalse(), isTrue()), "one");
    assertMatch(or(isTrue(), isFalse()), "one");
    assertNotMatch(or(isFalse(), isFalse()), "one");
  }

  @Test
  public void testToString() {
    assertEquals("(extends | implements)", or("extends", "implements").toString());
  }
}
