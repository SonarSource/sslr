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
  public void testSimpleRecursiveRule() throws Exception {
    RuleImpl recursiveRule = new LeftRecursiveRuleImpl("recursiveRule");
    recursiveRule.isOr(and(recursiveRule, "+", "1"), "1");

    assertThat(recursiveRule, match("1 + 1 + 1 + 1 + 1"));
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
    RuleImpl c = new RuleImpl("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a, match("x x y x y x y x y x y"));
  }

  @Test
  public void testMultipleSequentialCallsToMatch() throws Exception {
    RuleImpl a = new LeftRecursiveRuleImpl("a");
    RuleImpl b = new RuleImpl("b");
    RuleImpl c = new RuleImpl("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a, match("x x y x y x y x y x y"));
    assertThat(a, match("c x y"));
  }

  @Test
  public void testComplexeRecursion_OK1() throws Exception {
    RuleImpl pnae = new LeftRecursiveRuleImpl("pnae");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl inve = new RuleImpl("inve");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pnae, "MA");
    inve.is(pnae, "INVE");

    assertThat(pnae, match("PNAE MA"));
  }

  @Test
  public void testComplexeRecursion_OK2() throws Exception {
    RuleImpl pnae = new LeftRecursiveRuleImpl("pnae");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl pe = new RuleImpl("pe");

    pnae.isOr(ma, "PNAE");
    ma.is(pe, "MA");
    pe.isOr("PE", pnae);

    assertThat(pnae, match("PNAE MA"));
  }

  @Test
  public void testComplexeRecursion_OK3() throws Exception {
    RuleImpl pnae = new LeftRecursiveRuleImpl("pnae");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl inve = new RuleImpl("inve");
    RuleImpl pe = new RuleImpl("pe");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pe, "MA");
    inve.is(pe, "INVE");
    pe.isOr("PE", pnae);

    assertThat(pnae, match("PNAE MA"));
  }

  @Test
  public void testComplexeRecursion_NewOK1() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new RuleImpl("sn");
    // RuleImpl ma = new RuleImpl("ma");
    RuleImpl inve = new RuleImpl("inve");

    exp.isOr(inve, sn);
    sn.is("SN");
    // ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp, match("SN INVE"));
  }

  @Test
  public void testComplexeRecursion_NewOK2() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new RuleImpl("sn");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl inve = new RuleImpl("inve");

    // I add "ma" rule between "inve" and "sn" : OK
    exp.isOr(inve, ma, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp, match("SN INVE"));
  }

  @Test
  public void testComplexeRecursion_NewKO1() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new RuleImpl("sn");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl inve = new RuleImpl("inve");

    // I pass "ma" rule before "inve" rule : KO
    exp.isOr(ma, inve, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp, match("SN INVE"));
  }

  @Test
  public void testComplexeRecursion_NewKO2() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new LeftRecursiveRuleImpl("sn");
    RuleImpl ma = new LeftRecursiveRuleImpl("ma");
    RuleImpl inve = new LeftRecursiveRuleImpl("inve");

    // This is the exact way (order is important) it is described in the real C# grammar specification
    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    // And this should normally parse
    assertThat(exp, match("SN MA MA INVE"));
  }

}
