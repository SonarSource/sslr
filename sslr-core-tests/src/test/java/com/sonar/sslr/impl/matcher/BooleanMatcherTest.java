/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class BooleanMatcherTest {

  @Test
  public void ok() {
    assertThat(isTrue(), match("hehe"));
    assertThat(isFalse(), org.hamcrest.Matchers.not(match("hehe")));
  }

  @Test
  public void testToString() {
    assertEquals(isTrue().toString(), "isTrue()");
    assertEquals(isFalse().toString(), "isFalse()");
  }

  @Test
  public void testEqualsAndHashCode() {
    assertThat(isTrue() == isTrue(), is(true));
    assertThat(isFalse() == isFalse(), is(true));
    assertThat(isTrue() == isFalse(), is(false));
    assertThat(anyToken() == tillNewLine(), is(false));
  }

}
