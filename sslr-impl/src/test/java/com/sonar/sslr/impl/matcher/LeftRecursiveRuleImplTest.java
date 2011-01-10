/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.and;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LeftRecursiveRuleImplTest {

  private RuleImpl recursiveRule;

  @Before
  public void init() {
    recursiveRule = new LeftRecursiveRuleImpl("recursiveRule");
    recursiveRule.isOr(and(recursiveRule, "and", "one"), "one");
  }

  @Test
  public void testDetectLeftRecursion() throws Exception {
    assertThat(recursiveRule, match("one and one and one and one and one"));
  }

}
