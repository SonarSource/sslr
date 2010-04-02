/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl.matcher;

import org.junit.Test;

import static com.sonarsource.sslr.impl.matcher.Matchers.isFalse;
import static com.sonarsource.sslr.impl.matcher.Matchers.isTrue;
import static com.sonarsource.sslr.impl.matcher.Matchers.one2n;

import static org.junit.Assert.assertEquals;

public class OneToNMatcherTest extends MatcherCase {

  @Test
  public void testMany() {
    assertMatch(one2n(isTrue()), "one");
    assertNotMatch(one2n(isFalse()), "one");
  }

  @Test
  public void testToString() {
    assertEquals("(public void class)+", one2n("public", "void", "class").toString());
  }
}
