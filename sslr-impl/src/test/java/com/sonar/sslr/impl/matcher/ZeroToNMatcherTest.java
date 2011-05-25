/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Advanced.isFalse;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Advanced.isTrue;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.o2n;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ZeroToNMatcherTest  {

  @Test
  public void ok() {
    assertThat(o2n(isFalse()), not(match("one")));
    assertThat(o2n(isTrue()), match("one two three"));
  }
  
  @Test
  public void testToString() {
  	assertEquals(o2n("print").toString(), "o2n");
  }

}
