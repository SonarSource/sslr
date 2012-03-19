/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class GrammarAnalyserTest {

  @Test
  public void noRuleTest() {
    GrammarAnalyser analyser = new GrammarAnalyser(new NoRuleGrammar());
    assertThat(analyser.getRules().size(), is(0));
    assertThat(analyser.hasIssues(), is(false));
  }

  private static class NoRuleGrammar extends Grammar {

    public NoRuleGrammar() {
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void singleRuleNoIssuesTest() {
    SingleRuleNoIssuesGrammar grammar = new SingleRuleNoIssuesGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.rule)));
    assertThat(analyser.hasIssues(), is(false));

    RuleMatcher rule = getRuleMatcher(grammar.rule);
    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
  }

  private static class SingleRuleNoIssuesGrammar extends Grammar {

    public Rule rule;

    public SingleRuleNoIssuesGrammar() {
      rule.is("foo");
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void directLeftRecursiveRuleTest() {
    DirectLeftRecursiveGrammar grammar = new DirectLeftRecursiveGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.rule)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher rule = getRuleMatcher(grammar.rule);
    assertThat(analyser.isLeftRecursive(rule), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
    assertThat(analyser.getLeftRecursionxception(rule).getLeftRecursiveRule(), is(rule));
  }

  private static class DirectLeftRecursiveGrammar extends Grammar {

    public Rule rule;

    public DirectLeftRecursiveGrammar() {
      rule.is(rule);
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void indirectLeftRecursiveRuleTest() {
    IndirectLeftRecursiveGrammar grammar = new IndirectLeftRecursiveGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.ruleA, grammar.ruleB)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher ruleA = getRuleMatcher(grammar.ruleA);
    assertThat(analyser.isLeftRecursive(ruleA), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleA), is(false));
    assertThat(analyser.getLeftRecursionxception(ruleA).getLeftRecursiveRule(), is(ruleA));

    RuleMatcher ruleB = getRuleMatcher(grammar.ruleB);
    assertThat(analyser.isLeftRecursive(ruleB), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleB), is(false));
    assertThat(analyser.getLeftRecursionxception(ruleB).getLeftRecursiveRule(), is(ruleB));
  }

  private static class IndirectLeftRecursiveGrammar extends Grammar {

    public Rule ruleA;
    public Rule ruleB;

    public IndirectLeftRecursiveGrammar() {
      ruleA.is(ruleB);
      ruleB.is(ruleA);
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void dependOnLeftRecursiveRuleTest() {
    DependOnLeftRecursiveRuleGrammar grammar = new DependOnLeftRecursiveRuleGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.rule, grammar.leftRecursiveRule)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher rule = getRuleMatcher(grammar.rule);
    RuleMatcher leftRecursiveRule = getRuleMatcher(grammar.leftRecursiveRule);

    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(true));
    assertThat(analyser.getLeftRecursionxception(rule).getLeftRecursiveRule(), is(leftRecursiveRule));

    assertThat(analyser.isLeftRecursive(leftRecursiveRule), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(leftRecursiveRule), is(false));
    assertThat(analyser.getLeftRecursionxception(leftRecursiveRule).getLeftRecursiveRule(), is(leftRecursiveRule));
  }

  private static class DependOnLeftRecursiveRuleGrammar extends Grammar {

    public Rule rule;
    public Rule leftRecursiveRule;

    public DependOnLeftRecursiveRuleGrammar() {
      rule.is(leftRecursiveRule);
      leftRecursiveRule.is(leftRecursiveRule);
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  private RuleMatcher getRuleMatcher(Rule rule) {
    return ((RuleDefinition) rule).getRule();
  }

  private Set<RuleMatcher> getSet(Rule... rules) {
    Set<RuleMatcher> set = Sets.newHashSet();

    for (Rule rule : rules) {
      set.add(getRuleMatcher(rule));
    }

    return set;
  }

}
