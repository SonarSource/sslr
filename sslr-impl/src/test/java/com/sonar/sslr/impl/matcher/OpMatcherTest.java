/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import com.sonar.sslr.impl.matcher.Matchers;

import static com.sonar.sslr.impl.matcher.Matchers.isFalse;
import static com.sonar.sslr.impl.matcher.Matchers.isTrue;

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
