/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static com.sonar.sslr.impl.matcher.Matchers.and;
import static com.sonar.sslr.impl.matcher.Matchers.opt;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class LeftRecursiveRuleImplTest {

  @BeforeClass
  public static void initSslrMode() {
    // System.setProperty(ParserLogger.SSLR_MODE_PROPERTY, ParserLogger.SSLR_DEBUG_MODE);
  }

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
  public void testRecursionCase8() throws Exception {
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
  public void testRecursionCase5() throws Exception {
    RuleImpl pnae = new LeftRecursiveRuleImpl("pnae");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl inve = new RuleImpl("inve");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pnae, "MA");
    inve.is(pnae, "INVE");

    assertThat(pnae, match("PNAE MA"));
  }

  @Test
  public void testRecursionCase6() throws Exception {
    RuleImpl pnae = new LeftRecursiveRuleImpl("pnae");
    RuleImpl ma = new RuleImpl("ma");
    RuleImpl pe = new RuleImpl("pe");

    pnae.isOr(ma, "PNAE");
    ma.is(pe, "MA");
    pe.isOr("PE", pnae);

    assertThat(pnae, match("PNAE MA"));
  }

  @Test
  public void testRecursionCase7() throws Exception {
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
  public void testRecursionCase3() throws Exception {
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
  public void testRecursionCase4() throws Exception {
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
  public void testRecursionCase1() throws Exception {
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
  public void testRecursionCase2() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new LeftRecursiveRuleImpl("sn");
    RuleImpl ma = new LeftRecursiveRuleImpl("ma");
    RuleImpl inve = new LeftRecursiveRuleImpl("inve");

    // This is the exact way (order is important) it is described in the real C# grammar specification
    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp, match("SN MA MA INVE"));
  }

  @Test
  public void testRecursionCase9() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new LeftRecursiveRuleImpl("sn");
    RuleImpl ma = new LeftRecursiveRuleImpl("ma");
    RuleImpl singleMa = new LeftRecursiveRuleImpl("singleMa");
    RuleImpl inve = new LeftRecursiveRuleImpl("inve");

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(exp, "SOMETHING");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp, match("SN MA INVE"));
  }

  @Test
  public void testRecursionCase10() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new LeftRecursiveRuleImpl("sn");
    RuleImpl ma = new LeftRecursiveRuleImpl("ma");
    RuleImpl singleMa = new LeftRecursiveRuleImpl("singleMa");
    RuleImpl inve = new LeftRecursiveRuleImpl("inve");

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(opt(exp, "SOMETHING"), "MA");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp, match("SN MA INVE"));
  }

  @Test
  @Ignore("This is the exact same test case as #testRecursionCase2(), except that I added to assertions at the end, and they fail")
  public void testRecursionCase11() throws Exception {
    RuleImpl exp = new LeftRecursiveRuleImpl("exp");
    RuleImpl sn = new LeftRecursiveRuleImpl("sn");
    RuleImpl ma = new LeftRecursiveRuleImpl("ma");
    RuleImpl inve = new LeftRecursiveRuleImpl("inve");

    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    // this is the assertion that passes in #testRecursionCase2()
    assertThat(exp, match("SN MA MA INVE"));

    // this assertion fails
    assertThat(ma, match("SN MA MA"));

    // this one turns into an infinite loop
    assertThat(inve, match("SN INVE"));
  }

}
