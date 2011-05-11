/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.isFalse;
import static com.sonar.sslr.impl.matcher.Matchers.isTrue;
import static com.sonar.sslr.impl.matcher.Matchers.opt;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OptMatcherTest {

  @Test
  public void ok() {
    assertThat(opt(isFalse()), not(match("one")));
    assertThat(opt(isTrue()), match("one"));
  }

  @Test
  public void testGetDefinition() {
    assertEquals("opt(\"implements\")", Matchers.opt("implements").getDefinition());
    assertEquals("opt(and(\"implements\", \"b\"))", Matchers.opt("implements", "b").getDefinition());
  }
}
