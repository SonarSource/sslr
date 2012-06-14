/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.DelegatingMatcher;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import java.util.Set;

import static com.sonar.sslr.impl.analysis.FirstVisitor.first;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FirstVisitorTest {

  @Test
  public void tokenTest() {
    Matcher matcher = and("value");
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
      set.add(DelegatingMatcher.unwrap(and(tokenValue)));
    }

    return set;
  }

}
