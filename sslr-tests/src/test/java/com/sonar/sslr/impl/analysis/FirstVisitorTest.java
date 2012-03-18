/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.TokenMatcher;
import org.junit.Test;

import java.util.Set;

import static com.sonar.sslr.impl.analysis.FirstVisitor.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FirstVisitorTest {

  @Test
  public void tokenTest() {
    Matcher matcher = and("value");
    assertThat(matcher instanceof TokenMatcher, is(true));
    assertThat(first(matcher), is(getSet("value")));
  }

  @Test
  public void orTest() {
    assertThat(first(or("value")), is(getSet("value")));
    assertThat(first(or("value1", "value2")), is(getSet("value1", "value2")));
  }

  @Test
  public void andTest() {
    assertThat(first(and("value")), is(getSet("value")));
    assertThat(first(and("value1", "value2")), is(getSet("value1")));
    assertThat(first(and(opt("value1"), "value2")), is(getSet("value1", "value2")));
  }

  @Test
  public void optTest() {
    assertThat(first(opt("value")), is(getSet("value")));
  }

  @Test
  public void one2nTest() {
    assertThat(first(one2n("value")), is(getSet("value")));
  }

  @Test
  public void ruleMatcherTest() {
    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule").is("value");
    assertThat(first(rule.getRule()), is(getSet("value")));
  }

  @Test
  public void compoundTest() {
    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");

    rule.is(or(
        "foo",
        "bar",
        and(
            opt(
                "huhu",
                one2n(rule)
            ),
            "hehe",
            "haha"
        )));

    assertThat(first(rule.getRule()), is(getSet("foo", "bar", "huhu", "hehe")));
  }

  @Test(expected = LeftRecursionException.class)
  public void directLeftRecursionTest() {
    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");
    rule.is(rule);

    first(rule.getRule());
  }

  @Test(expected = LeftRecursionException.class)
  public void indirectLeftRecursionTest() {
    RuleDefinition rule1 = RuleDefinition.newRuleBuilder("rule1");
    RuleDefinition rule2 = RuleDefinition.newRuleBuilder("rule2");

    rule1.is(rule2);
    rule2.is(rule1);

    first(rule1.getRule());
  }

  @Test
  public void falsePositiveRecursionTest() {
    RuleDefinition rule1 = RuleDefinition.newRuleBuilder("rule1");
    RuleDefinition rule2 = RuleDefinition.newRuleBuilder("rule2");

    rule1.is(or(rule2, rule2));
    rule2.is("value");

    assertThat(first(rule1.getRule()), is(getSet("value")));
  }

  @Test(expected = LeftRecursionException.class)
  public void deeplyNestedRecursion() {
    RuleDefinition rule = RuleDefinition.newRuleBuilder("rule");

    rule.is(or(
        "foo",
        "bar",
        and(
            opt(
                opt("huhu"),
                one2n(rule)
            ),
            "hehe",
            "haha"
        )));

    first(rule.getRule());
  }

  private Set<Matcher> getSet(String... tokenValues) {
    Set<Matcher> set = Sets.newHashSet();

    for (String tokenValue : tokenValues) {
      set.add(and(tokenValue));
    }

    return set;
  }

}
