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

import org.junit.Ignore;
import org.junit.Test;


public class LeftRecursiveRuleMatcherTest {

  @Test
  public void testSimpleRecursiveRule() throws Exception {
    RuleDefinition recursiveRule = RuleDefinition.newLeftRecursiveRuleBuilder("recursiveRule");
    recursiveRule.isOr("1", and(recursiveRule, "+", "1"));

    assertThat(recursiveRule.getRule(), match("1 + 1 + 1 + 1"));
  }

  @Test
  public void testPreventMatchersToConsumeTokens() throws Exception {
    RuleDefinition rule = RuleDefinition.newLeftRecursiveRuleBuilder("rule");
    rule.isOr("y", and(rule, "and", "x"), "z");
    assertThat(rule.getRule(), not(match("z and x y")));
  }

  @Test
  public void testRecursionCase8() throws Exception {
    RuleDefinition a = RuleDefinition.newLeftRecursiveRuleBuilder("a");
    RuleDefinition b = RuleDefinition.newLeftRecursiveRuleBuilder("b");
    RuleDefinition c = RuleDefinition.newRuleBuilder("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a.getRule(), match("x x y x y x y x y x y"));
  }

  @Test
  public void testMultipleSequentialCallsToMatch() throws Exception {
    RuleDefinition a = RuleDefinition.newLeftRecursiveRuleBuilder("a");
    RuleDefinition b = RuleDefinition.newRuleBuilder("b");
    RuleDefinition c = RuleDefinition.newRuleBuilder("c");

    a.isOr(and(b, "x", "y"), "x");
    b.is(c);
    c.isOr(a, "c");

    assertThat(a.getRule(), match("x x y x y x y x y x y"));
    assertThat(a.getRule(), match("c x y"));
  }

  @Test
  public void testRecursionCase5() throws Exception {
    RuleDefinition pnae = RuleDefinition.newLeftRecursiveRuleBuilder("pnae");
    RuleDefinition ma = RuleDefinition.newRuleBuilder("ma");
    RuleDefinition inve = RuleDefinition.newRuleBuilder("inve");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pnae, "MA");
    inve.is(pnae, "INVE");

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase6() throws Exception {
    RuleDefinition pnae = RuleDefinition.newLeftRecursiveRuleBuilder("pnae");
    RuleDefinition ma = RuleDefinition.newRuleBuilder("ma");
    RuleDefinition pe = RuleDefinition.newRuleBuilder("pe");

    pnae.isOr(ma, "PNAE");
    ma.is(pe, "MA");
    pe.isOr("PE", pnae);

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase7() throws Exception {
    RuleDefinition pnae = RuleDefinition.newLeftRecursiveRuleBuilder("pnae");
    RuleDefinition ma = RuleDefinition.newRuleBuilder("ma");
    RuleDefinition inve = RuleDefinition.newRuleBuilder("inve");
    RuleDefinition pe = RuleDefinition.newRuleBuilder("pe");

    pnae.isOr(ma, inve, "PNAE");
    ma.is(pe, "MA");
    inve.is(pe, "INVE");
    pe.isOr("PE", pnae);

    assertThat(pnae.getRule(), match("PNAE MA"));
  }

  @Test
  public void testRecursionCase3() throws Exception {
    RuleDefinition exp = RuleDefinition.newLeftRecursiveRuleBuilder("exp");
    RuleDefinition sn = RuleDefinition.newRuleBuilder("sn");
    RuleDefinition inve = RuleDefinition.newRuleBuilder("inve");

    exp.isOr(inve, sn);
    sn.is("SN");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase4() throws Exception {
    RuleDefinition exp = RuleDefinition.newLeftRecursiveRuleBuilder("exp");
    RuleDefinition sn = RuleDefinition.newRuleBuilder("sn");
    RuleDefinition ma = RuleDefinition.newRuleBuilder("ma");
    RuleDefinition inve = RuleDefinition.newRuleBuilder("inve");

    // I add "ma" rule between "inve" and "sn" : OK
    exp.isOr(inve, ma, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase1() throws Exception {
    RuleDefinition exp = RuleDefinition.newLeftRecursiveRuleBuilder("exp");
    RuleDefinition sn = RuleDefinition.newRuleBuilder("sn");
    RuleDefinition ma = RuleDefinition.newRuleBuilder("ma");
    RuleDefinition inve = RuleDefinition.newRuleBuilder("inve");

    // I pass "ma" rule before "inve" rule : KO
    exp.isOr(ma, inve, sn);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN INVE"));
  }

  @Test
  public void testRecursionCase2() throws Exception {
    RuleDefinition exp = RuleDefinition.newLeftRecursiveRuleBuilder("exp");
    RuleDefinition sn = RuleDefinition.newLeftRecursiveRuleBuilder("sn");
    RuleDefinition ma = RuleDefinition.newLeftRecursiveRuleBuilder("ma");
    RuleDefinition inve = RuleDefinition.newLeftRecursiveRuleBuilder("inve");

    // This is the exact way (order is important) it is described in the real C# grammar specification
    exp.isOr(sn, ma, inve);
    sn.is("SN");
    ma.is(exp, "MA");
    inve.is(exp, "INVE");

    assertThat(exp.getRule(), match("SN MA MA INVE"));
  }

  @Test
  public void testRecursionCase9() throws Exception {
    RuleDefinition exp = RuleDefinition.newLeftRecursiveRuleBuilder("exp");
    RuleDefinition sn = RuleDefinition.newLeftRecursiveRuleBuilder("sn");
    RuleDefinition ma = RuleDefinition.newLeftRecursiveRuleBuilder("ma");
    RuleDefinition singleMa = RuleDefinition.newLeftRecursiveRuleBuilder("singleMa");
    RuleDefinition inve = RuleDefinition.newLeftRecursiveRuleBuilder("inve");

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(exp, "SOMETHING");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp.getRule(), match("SN MA INVE"));
  }

  @Test
  public void testRecursionCase10() throws Exception {
    RuleDefinition exp = RuleDefinition.newLeftRecursiveRuleBuilder("exp");
    RuleDefinition sn = RuleDefinition.newLeftRecursiveRuleBuilder("sn");
    RuleDefinition ma = RuleDefinition.newLeftRecursiveRuleBuilder("ma");
    RuleDefinition singleMa = RuleDefinition.newLeftRecursiveRuleBuilder("singleMa");
    RuleDefinition inve = RuleDefinition.newLeftRecursiveRuleBuilder("inve");

    exp.isOr(sn, inve, singleMa, ma);
    sn.is("SN");
    inve.is(opt(exp, "SOMETHING"), "MA");
    singleMa.is("MA");
    ma.is(exp, "MA", "INVE");

    assertThat(exp.getRule(), match("SN MA INVE"));
  }

}
