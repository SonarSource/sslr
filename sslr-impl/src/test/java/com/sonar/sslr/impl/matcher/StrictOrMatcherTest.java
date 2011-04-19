/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.isFalse;
import static com.sonar.sslr.impl.matcher.Matchers.isTrue;
import static com.sonar.sslr.impl.matcher.Matchers.or;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StrictOrMatcherTest {

  @Before
  public void init() {
    Matchers.activeStrictOrMode();
  }

  @After
  public void detroy() {
    Matchers.deactivateStrictOrMode();
  }

  @Test
  public void ok() {
    assertThat(or(isFalse(), isTrue()), match("one"));
    assertThat(or(isTrue(), isFalse()), match("one"));
    assertThat(or(isFalse(), isFalse()), not(match("one")));
  }

  @Test(expected = IllegalStateException.class)
  public void testTwoWaysMatch() {
    assertThat(or(isTrue(), isTrue()), match("one"));
  }

  @Test
  public void testToString() {
    assertEquals("(extends | implements)", or("extends", "implements").toString());
  }
}
