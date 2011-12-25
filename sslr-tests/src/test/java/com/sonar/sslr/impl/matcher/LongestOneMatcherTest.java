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

public class LongestOneMatcherTest {

  @Test
  public void ok() {
    assertThat(longestOne("hehe", "hehe"), match("hehe")); /* No strict mode */
    assertThat(longestOne("hehe", and("hehe", "haha")), match("hehe haha"));
    assertThat(longestOne(and("hehe", "haha"), "hehe"), match("hehe haha"));
    assertThat(longestOne("hehe", and("hehe", "haha")), not(match("hehe haha huhu")));
    assertThat(longestOne(and("hehe", "haha"), "hehe"), not(match("hehe haha huhu")));
  }

  @Test
  public void testToString() {
    assertEquals(longestOne("(").toString(), "longestOne");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(longestOne("a", "a") == longestOne("a", "a"), is(true));
    assertThat(longestOne("a", "a") == longestOne("a", "b"), is(false));
    assertThat(longestOne("a", "a") == and("a", "a"), is(false));
  }

}
