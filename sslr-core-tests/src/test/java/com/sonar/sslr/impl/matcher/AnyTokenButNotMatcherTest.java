/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.is;
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
  
  @Test
  public void testEqualsAndHashCode() {
  	assertThat(anyTokenButNot("a") == anyTokenButNot("a"), is(true));
  	assertThat(anyTokenButNot("a") == anyTokenButNot("b"), is(false));
  	assertThat(anyTokenButNot("a") == adjacent("a"), is(false));
  }

}
