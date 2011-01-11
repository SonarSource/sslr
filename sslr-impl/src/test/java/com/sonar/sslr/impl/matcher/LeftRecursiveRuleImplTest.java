/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.and;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LeftRecursiveRuleImplTest {

  @Test
  public void testDirectLeftRecursion() throws Exception {
    RuleImpl rule = new LeftRecursiveRuleImpl("rule");
    rule.isOr(and(rule, "and", "x"), "x");
    assertThat(rule, match("x and x and x and x and x"));
  }
  
  @Test
  public void testPreventMatchersToConsumeTokens() throws Exception {
    RuleImpl rule = new LeftRecursiveRuleImpl("rule");
    rule.isOr("y", and(rule, "and", "x"), "z");
    assertThat(rule, not(match("z and x y")));
  }

  @Test
  public void testInDirectLeftRecursion() throws Exception {
    RuleImpl a = new LeftRecursiveRuleImpl("a");
    RuleImpl b = new LeftRecursiveRuleImpl("b");
    RuleImpl c = new LeftRecursiveRuleImpl("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a, match("x x y x y x y x y x y"));
  }
}
