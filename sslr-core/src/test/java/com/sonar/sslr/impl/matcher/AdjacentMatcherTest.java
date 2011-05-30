/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.adjacent;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AdjacentMatcherTest {

  @Test
  public void ok() {
    assertThat(and("myMacro", adjacent("(")), match("myMacro("));
    assertThat(and("myMacro", adjacent("(")), not(match("myMacro (")));
  }

  @Test
  public void testToString() {
    assertEquals(adjacent("(").toString(), "adjacent");
  }

}
