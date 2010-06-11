/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.isFalse;
import static com.sonar.sslr.impl.matcher.Matchers.isTrue;
import static com.sonar.sslr.impl.matcher.Matchers.strictOr;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StrictOrMatcherTest {

  @Test
  public void testStrictOr() {
    assertThat(strictOr(isFalse(), isTrue()), match("one"));
    assertThat(strictOr(isTrue(), isFalse()), match("one"));
    assertThat(strictOr(isFalse(), isFalse()), not(match("one")));
  }

  @Test(expected = IllegalStateException.class)
  public void testTwoWaysMatch() {
    assertThat(strictOr(isTrue(), isTrue()), match("one"));
  }

  @Test
  public void testToString() {
    assertEquals("(extends | implements)", strictOr("extends", "implements").toString());
  }
}
