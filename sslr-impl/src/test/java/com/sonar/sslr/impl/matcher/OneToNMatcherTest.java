/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Advanced.isFalse;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Advanced.isTrue;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.one2n;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OneToNMatcherTest {

  @Test
  public void ok() {
    assertThat(one2n(isTrue()), match("one"));
    assertThat(one2n(isFalse()), not(match("one")));
  }

  @Test
  public void testToString() {
  	assertEquals(one2n("(").toString(), "one2n");
  }
  
}
