/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.TokenMatcher;
import org.junit.Test;

import static com.sonar.sslr.impl.analysis.EmptyVisitor.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class EmptyVisitorTest {

  @Test
  public void tokenTest() {
    Matcher matcher = and("value");
    assertThat(matcher instanceof TokenMatcher, is(true));
    assertThat(empty(matcher), is(false));
  }

  @Test
  public void orTest() {
    assertThat(empty(or("value1", "value2")), is(false));
    assertThat(empty(or(opt("value1"), "value2")), is(true));
    assertThat(empty(or("value1", opt("value2"))), is(true));
    assertThat(empty(or(opt("value1"), opt("value2"))), is(true));
  }

  @Test
  public void andTest() {
    assertThat(empty(and("value1", "value2")), is(false));
    assertThat(empty(and(opt("value1"), "value2")), is(false));
    assertThat(empty(and("value1", opt("value2"))), is(false));
    assertThat(empty(and(opt("value1"), opt("value2"))), is(true));
  }

  @Test
  public void optTest() {
    assertThat(empty(opt("value")), is(true));
  }

  @Test
  public void one2nTest() {
    assertThat(empty(one2n("value")), is(false));
    assertThat(empty(one2n(opt("value"))), is(true));
  }

  @Test
  public void ruleMatcherTest() {
    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule").is("value");
    assertThat(empty(rule.getRule()), is(false));

    rule = RuleDefinition.newRuleBuilder("rule").is(opt("value"));
    assertThat(empty(rule.getRule()), is(true));
  }

  @Test(expected = LeftRecursionException.class)
  public void directLeftRecursionTest() {
    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");
    rule.is(rule);

    empty(rule.getRule());
  }

  @Test(expected = LeftRecursionException.class)
  public void indirectLeftRecursionTest() {
    RuleDefinition rule1 = RuleDefinition.newRuleBuilder("rule1");
    RuleDefinition rule2 = RuleDefinition.newRuleBuilder("rule2");

    rule1.is(rule2);
    rule2.is(rule1);

    empty(rule1.getRule());
  }

  @Test
  public void falsePositiveRecursionTest() {
    RuleDefinition rule1 = RuleDefinition.newRuleBuilder("rule1");
    RuleDefinition rule2 = RuleDefinition.newRuleBuilder("rule2");

    rule1.is(or(rule2, rule2));
    rule2.is("value");

    assertThat(empty(rule1.getRule()), is(false));
  }

}
