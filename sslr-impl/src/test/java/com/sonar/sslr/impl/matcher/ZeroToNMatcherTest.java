/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import org.junit.Test;

import static com.sonar.sslr.impl.matcher.Matchers.isFalse;
import static com.sonar.sslr.impl.matcher.Matchers.isTrue;
import static com.sonar.sslr.impl.matcher.Matchers.o2n;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;

public class ZeroToNMatcherTest  {

  @Test
  public void ok() {
    assertThat(o2n(isFalse()), not(match("one")));
    assertThat(o2n(isTrue()), match("one two three"));
  }

  @Test
  public void testToString() {
    assertEquals("(public void class)*", o2n("public", "void", "class").toString());
  }
}
