/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import org.junit.Test;

import static com.sonarsource.sslr.matcher.Matchers.isFalse;
import static com.sonarsource.sslr.matcher.Matchers.isTrue;
import static com.sonarsource.sslr.matcher.Matchers.o2n;

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
