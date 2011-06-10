/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.anyTokenButNot;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AnyTokenButNotMatcherTest {

  @Test
  public void ok() {
    assertThat(anyTokenButNot("two"), match("one"));
    assertThat(anyTokenButNot("one"), not(match("one")));
  }

  @Test
  public void testToString() {
    assertEquals(anyTokenButNot("(").toString(), "anyTokenButNot");
  }

}
