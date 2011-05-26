/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.CfgFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LeftRecursiveRuleMatcherTest {

  @Test
  public void testSimpleRecursiveRule() throws Exception {
    RuleBuilder recursiveRule = new RuleBuilder("recursiveRule", true);
    recursiveRule.isOr("1", and(recursiveRule, "+", "1"));

    assertThat(recursiveRule.getRule(), match("1 + 1 + 1 + 1"));
  }

  @Test
  public void testPreventMatchersToConsumeTokens() throws Exception {
    RuleBuilder rule = new RuleBuilder("rule", true);
    rule.isOr("y", and(rule, "and", "x"), "z");
    assertThat(rule.getRule(), not(match("z and x y")));
  }

  @Test
  public void testRecursionCase8() throws Exception {
    RuleBuilder a = new RuleBuilder("a", true);
    RuleBuilder b = new RuleBuilder("b", true);
    RuleBuilder c = new RuleBuilder("c", false);

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a.getRule(), match("x x y x y x y x y x y"));
  }

  @Test
  public void testMultipleSequentialCallsToMatch() throws Exception {
    RuleBuilder a = new RuleBuilder("a", true);
    RuleBuilder b = new RuleBuilder("b", false);
    RuleBuilder c = new RuleBuilder("c", false);

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a.getRule(), match("x x y x y x y x y x y"));
    assertThat(a.getRule(), match("c x y"));
  }

  @Test
  public void testRecursionCase5() throws Exception {
    RuleBuilder pnae = new RuleBuilder("pnae", true);
    RuleBuilder ma = new RuleBuilder("ma", false);
    RuleBuilder inve = new RuleBuilder("inve", false);

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pnae, "MA");
    inve.is(pnae, "INVE");

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase6() throws Exception {
    RuleBuilder pnae = new RuleBuilder("pnae", true);
    RuleBuilder ma = new RuleBuilder("ma", false);
    RuleBuilder pe = new RuleBuilder("pe", false);

    pnae.isOr(ma, "PNAE");
    ma.is(pe, "MA");
    pe.isOr("PE", pnae);

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase7() throws Exception {
    RuleBuilder pnae = new RuleBuilder("pnae", true);
    RuleBuilder ma = new RuleBuilder("ma", false);
    RuleBuilder inve = new RuleBuilder("inve", false);
    RuleBuilder pe = new RuleBuilder("pe", false);

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pe, "MA");
    inve.is(pe, "INVE");
    pe.isOr("PE", pnae);

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase3() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", false);
    RuleBuilder inve = new RuleBuilder("inve", false);

    exp.isOr(inve, sn);
    sn.is("SN");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase4() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", false);
    RuleBuilder ma = new RuleBuilder("ma", false);
    RuleBuilder inve = new RuleBuilder("inve", false);

    // I add "ma" rule between "inve" and "sn" : OK
    exp.isOr(inve, ma, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase1() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", false);
    RuleBuilder ma = new RuleBuilder("ma", false);
    RuleBuilder inve = new RuleBuilder("inve", false);

    // I pass "ma" rule before "inve" rule : KO
    exp.isOr(ma, inve, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase2() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", true);
    RuleBuilder ma = new RuleBuilder("ma", true);
    RuleBuilder inve = new RuleBuilder("inve", true);

    // This is the exact way (order is important) it is described in the real C# grammar specification
    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN MA MA INVE"));
  }

  @Test
  public void testRecursionCase9() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", true);
    RuleBuilder ma = new RuleBuilder("ma", true);
    RuleBuilder singleMa = new RuleBuilder("singleMa", true);
    RuleBuilder inve = new RuleBuilder("inve", true);

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(exp, "SOMETHING");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp.getRule(), match("SN MA INVE"));
  }

  @Test
  public void testRecursionCase10() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", true);
    RuleBuilder ma = new RuleBuilder("ma", true);
    RuleBuilder singleMa = new RuleBuilder("singleMa", true);
    RuleBuilder inve = new RuleBuilder("inve", true);

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(opt(exp, "SOMETHING"), "MA");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp.getRule(), match("SN MA INVE"));
  }

  @Test
  public void testRecursionCase11() throws Exception {
    RuleBuilder exp = new RuleBuilder("exp", true);
    RuleBuilder sn = new RuleBuilder("sn", true);
    RuleBuilder ma = new RuleBuilder("ma", true);
    RuleBuilder inve = new RuleBuilder("inve", true);

    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(ma.getRule(), match("SN MA MA"));
    assertThat(inve.getRule(), match("SN INVE"));
    assertThat(inve.getRule(), match("SN MA INVE MA INVE"));

  }

}
