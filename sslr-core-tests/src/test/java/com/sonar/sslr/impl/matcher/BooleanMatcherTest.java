/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
  
}
