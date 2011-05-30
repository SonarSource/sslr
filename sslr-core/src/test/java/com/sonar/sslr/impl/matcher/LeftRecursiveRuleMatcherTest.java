/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.HamcrestMatchMatcher.match;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LeftRecursiveRuleMatcherTest {

  @Test
  public void testSimpleRecursiveRule() throws Exception {
    RuleBuilder recursiveRule = RuleBuilder.newLeftRecursiveRuleBuilder("recursiveRule");
    recursiveRule.isOr("1", and(recursiveRule, "+", "1"));

    assertThat(recursiveRule.getRule(), match("1 + 1 + 1 + 1"));
  }

  @Test
  public void testPreventMatchersToConsumeTokens() throws Exception {
    RuleBuilder rule = RuleBuilder.newLeftRecursiveRuleBuilder("rule");
    rule.isOr("y", and(rule, "and", "x"), "z");
    assertThat(rule.getRule(), not(match("z and x y")));
  }

  @Test
  public void testRecursionCase8() throws Exception {
    RuleBuilder a = RuleBuilder.newLeftRecursiveRuleBuilder("a");
    RuleBuilder b = RuleBuilder.newLeftRecursiveRuleBuilder("b");
    RuleBuilder c = RuleBuilder.newRuleBuilder("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a.getRule(), match("x x y x y x y x y x y"));
  }

  @Test
  public void testMultipleSequentialCallsToMatch() throws Exception {
    RuleBuilder a = RuleBuilder.newLeftRecursiveRuleBuilder("a");
    RuleBuilder b = RuleBuilder.newRuleBuilder("b");
    RuleBuilder c = RuleBuilder.newRuleBuilder("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a.getRule(), match("x x y x y x y x y x y"));
    assertThat(a.getRule(), match("c x y"));
  }

  @Test
  public void testRecursionCase5() throws Exception {
    RuleBuilder pnae = RuleBuilder.newLeftRecursiveRuleBuilder("pnae");
    RuleBuilder ma = RuleBuilder.newRuleBuilder("ma");
    RuleBuilder inve = RuleBuilder.newRuleBuilder("inve");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pnae, "MA");
    inve.is(pnae, "INVE");

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase6() throws Exception {
    RuleBuilder pnae = RuleBuilder.newLeftRecursiveRuleBuilder("pnae");
    RuleBuilder ma = RuleBuilder.newRuleBuilder("ma");
    RuleBuilder pe = RuleBuilder.newRuleBuilder("pe");

    pnae.isOr(ma, "PNAE");
    ma.is(pe, "MA");
    pe.isOr("PE", pnae);

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase7() throws Exception {
    RuleBuilder pnae = RuleBuilder.newLeftRecursiveRuleBuilder("pnae");
    RuleBuilder ma = RuleBuilder.newRuleBuilder("ma");
    RuleBuilder inve = RuleBuilder.newRuleBuilder("inve");
    RuleBuilder pe = RuleBuilder.newRuleBuilder("pe");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pe, "MA");
    inve.is(pe, "INVE");
    pe.isOr("PE", pnae);

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase3() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newRuleBuilder("sn");
    RuleBuilder inve = RuleBuilder.newRuleBuilder("inve");

    exp.isOr(inve, sn);
    sn.is("SN");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase4() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newRuleBuilder("sn");
    RuleBuilder ma = RuleBuilder.newRuleBuilder("ma");
    RuleBuilder inve = RuleBuilder.newRuleBuilder("inve");

    // I add "ma" rule between "inve" and "sn" : OK
    exp.isOr(inve, ma, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase1() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newRuleBuilder("sn");
    RuleBuilder ma = RuleBuilder.newRuleBuilder("ma");
    RuleBuilder inve = RuleBuilder.newRuleBuilder("inve");

    // I pass "ma" rule before "inve" rule : KO
    exp.isOr(ma, inve, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase2() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newLeftRecursiveRuleBuilder("sn");
    RuleBuilder ma = RuleBuilder.newLeftRecursiveRuleBuilder("ma");
    RuleBuilder inve = RuleBuilder.newLeftRecursiveRuleBuilder("inve");

    // This is the exact way (order is important) it is described in the real C# grammar specification
    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN MA MA INVE"));
  }

  @Test
  public void testRecursionCase9() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newLeftRecursiveRuleBuilder("sn");
    RuleBuilder ma = RuleBuilder.newLeftRecursiveRuleBuilder("ma");
    RuleBuilder singleMa = RuleBuilder.newLeftRecursiveRuleBuilder("singleMa");
    RuleBuilder inve = RuleBuilder.newLeftRecursiveRuleBuilder("inve");

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(exp, "SOMETHING");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp.getRule(), match("SN MA INVE"));
  }

  @Test
  public void testRecursionCase10() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newLeftRecursiveRuleBuilder("sn");
    RuleBuilder ma = RuleBuilder.newLeftRecursiveRuleBuilder("ma");
    RuleBuilder singleMa = RuleBuilder.newLeftRecursiveRuleBuilder("singleMa");
    RuleBuilder inve = RuleBuilder.newLeftRecursiveRuleBuilder("inve");

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(opt(exp, "SOMETHING"), "MA");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp.getRule(), match("SN MA INVE"));
  }

  @Test
  public void testRecursionCase11() throws Exception {
    RuleBuilder exp = RuleBuilder.newLeftRecursiveRuleBuilder("exp");
    RuleBuilder sn = RuleBuilder.newLeftRecursiveRuleBuilder("sn");
    RuleBuilder ma = RuleBuilder.newLeftRecursiveRuleBuilder("ma");
    RuleBuilder inve = RuleBuilder.newLeftRecursiveRuleBuilder("inve");

    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(ma.getRule(), match("SN MA MA"));
    assertThat(inve.getRule(), match("SN INVE"));
    assertThat(inve.getRule(), match("SN MA INVE MA INVE"));

  }

}
