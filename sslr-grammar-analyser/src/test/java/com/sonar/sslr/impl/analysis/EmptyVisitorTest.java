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

import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.junit.Test;

import static com.sonar.sslr.impl.analysis.EmptyVisitor.empty;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EmptyVisitorTest {

  @Test
  public void tokenTest() {
    Matcher matcher = and("value");
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
            opt("hehe"),
            opt("haha")
        )));

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

  @Test
  public void deeplyNestedRecursionShortcutByOpt() {
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

    assertThat(empty(rule.getRule()), is(false));
  }

}
