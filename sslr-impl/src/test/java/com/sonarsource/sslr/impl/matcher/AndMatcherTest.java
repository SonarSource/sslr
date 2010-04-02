/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import org.junit.Test;

import static com.sonarsource.sslr.impl.matcher.Matchers.and;
import static com.sonarsource.sslr.impl.matcher.Matchers.isFalse;
import static com.sonarsource.sslr.impl.matcher.Matchers.isTrue;

import static org.junit.Assert.assertEquals;

public class AndMatcherTest extends MatcherCase {

  @Test
  public void testAll() {
    assertMatch(and(isTrue(), isTrue(), isTrue()), "one", "two", "three");
    assertNotMatch(and(isTrue(), isFalse()), "one", "two");
    assertNotMatch(and(isFalse(), isFalse()), "one", "two");
  }

  @Test
  public void testToString() {
    assertEquals("public class MyClass", and("public", "class", "MyClass").toString());
  }
}
