/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

  @Test
  public void testEqualsAndHashCode() {
    assertThat(adjacent("a") == adjacent("a"), is(true));
    assertThat(adjacent("a") == adjacent("b"), is(false));
    assertThat(adjacent("a") == anyTokenButNot("a"), is(false));
  }

}
