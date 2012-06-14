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
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.*;
import org.junit.Test;

import java.util.Set;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.till;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
  public void unsupportedMatcherTest() {
    UnsupportedMatcherGrammar grammar = new UnsupportedMatcherGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.rule)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher rule = getRuleMatcher(grammar.rule);
    assertThat(analyser.hasIssues(rule), is(true));
    assertThat(analyser.isSkipped(rule), is(true));
    Exception e = analyser.getSkippedCause(rule);
    assertThat(e instanceof UnsupportedMatcherException, is(true));
    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
    assertThat(analyser.hasEmptyRepetitions(rule), is(false));
    assertThat(analyser.hasEmptyAlternatives(rule), is(false));
  }

  private static class UnsupportedMatcherGrammar extends Grammar {

    public Rule rule;

    public UnsupportedMatcherGrammar() {
      rule.is(till(EOF));
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
    assertThat(analyser.hasIssues(rule), is(false));
    assertThat(analyser.isSkipped(rule), is(false));
    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
    assertThat(analyser.hasEmptyRepetitions(rule), is(false));
    assertThat(analyser.hasEmptyAlternatives(rule), is(false));
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
    assertThat(analyser.hasIssues(rule), is(true));
    assertThat(analyser.isLeftRecursive(rule), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
    assertThat(analyser.getLeftRecursionException(rule).getLeftRecursiveRule(), is(rule));
    assertThat(analyser.hasEmptyRepetitions(rule), is(false));
    assertThat(analyser.hasEmptyAlternatives(rule), is(false));
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
    assertThat(analyser.hasIssues(ruleA), is(true));
    assertThat(analyser.isLeftRecursive(ruleA), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleA), is(false));
    assertThat(analyser.getLeftRecursionException(ruleA).getLeftRecursiveRule(), is(ruleA));
    assertThat(analyser.hasEmptyRepetitions(ruleA), is(false));
    assertThat(analyser.hasEmptyAlternatives(ruleA), is(false));

    RuleMatcher ruleB = getRuleMatcher(grammar.ruleB);
    assertThat(analyser.hasIssues(ruleB), is(true));
    assertThat(analyser.isLeftRecursive(ruleB), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleB), is(false));
    assertThat(analyser.getLeftRecursionException(ruleB).getLeftRecursiveRule(), is(ruleB));
    assertThat(analyser.hasEmptyRepetitions(ruleB), is(false));
    assertThat(analyser.hasEmptyAlternatives(ruleB), is(false));
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

    assertThat(analyser.hasIssues(rule), is(true));
    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(true));
    assertThat(analyser.getLeftRecursionException(rule).getLeftRecursiveRule(), is(leftRecursiveRule));
    assertThat(analyser.hasEmptyRepetitions(rule), is(false));
    assertThat(analyser.hasEmptyAlternatives(rule), is(false));

    assertThat(analyser.hasIssues(leftRecursiveRule), is(true));
    assertThat(analyser.isLeftRecursive(leftRecursiveRule), is(true));
    assertThat(analyser.isDependingOnLeftRecursiveRule(leftRecursiveRule), is(false));
    assertThat(analyser.getLeftRecursionException(leftRecursiveRule).getLeftRecursiveRule(), is(leftRecursiveRule));
    assertThat(analyser.hasEmptyRepetitions(leftRecursiveRule), is(false));
    assertThat(analyser.hasEmptyAlternatives(leftRecursiveRule), is(false));
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

  @Test
  public void singleEmptyRepetitionTest() {
    SingleEmptyRepetitionGrammar grammar = new SingleEmptyRepetitionGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.rule)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher rule = getRuleMatcher(grammar.rule);
    assertThat(analyser.hasIssues(rule), is(true));
    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
    assertThat(analyser.hasEmptyRepetitions(rule), is(true));
    assertThat(analyser.getEmptyRepetitions(rule), is((Set) Sets.newHashSet(DelegatingMatcher.unwrap(grammar.emptyRepetition))));
    assertThat(analyser.hasEmptyAlternatives(rule), is(false));
  }

  private static class SingleEmptyRepetitionGrammar extends Grammar {

    public Rule rule;
    public Matcher emptyRepetition = one2n(opt("hello"));

    public SingleEmptyRepetitionGrammar() {
      rule.is(emptyRepetition);
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void multipleEmptyRepetitionsTest() {
    MultipleEmptyRepetitionsGrammar grammar = new MultipleEmptyRepetitionsGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.ruleA, grammar.ruleB, grammar.ruleC)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher ruleA = getRuleMatcher(grammar.ruleA);
    assertThat(analyser.hasIssues(ruleA), is(true));
    assertThat(analyser.isLeftRecursive(ruleA), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleA), is(false));
    assertThat(analyser.hasEmptyRepetitions(ruleA), is(true));
    assertThat(analyser.getEmptyRepetitions(ruleA),
        is((Set) Sets.newHashSet(DelegatingMatcher.unwrap(grammar.emptyRepetitionRuleA1), DelegatingMatcher.unwrap(grammar.emptyRepetitionRuleA2))));
    assertThat(analyser.hasEmptyAlternatives(ruleA), is(false));

    RuleMatcher ruleB = getRuleMatcher(grammar.ruleB);
    assertThat(analyser.hasIssues(ruleB), is(true));
    assertThat(analyser.isLeftRecursive(ruleB), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleB), is(false));
    assertThat(analyser.hasEmptyRepetitions(ruleB), is(true));
    assertThat(analyser.getEmptyRepetitions(ruleB), is((Set) Sets.newHashSet(DelegatingMatcher.unwrap(grammar.emptyRepetitionRuleB))));
    assertThat(analyser.hasEmptyAlternatives(ruleB), is(false));

    RuleMatcher ruleC = getRuleMatcher(grammar.ruleC);
    assertThat(analyser.hasIssues(ruleC), is(false));
    assertThat(analyser.isLeftRecursive(ruleC), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleC), is(false));
    assertThat(analyser.hasEmptyRepetitions(ruleC), is(false));
    assertThat(analyser.hasEmptyAlternatives(ruleC), is(false));
  }

  private static class MultipleEmptyRepetitionsGrammar extends Grammar {

    public Rule ruleA;
    public Rule ruleB;
    public Rule ruleC;

    public Matcher emptyRepetitionRuleA1 = one2n(opt("hello"));
    public Matcher emptyRepetitionRuleA2 = one2n(opt("world"));
    public Matcher emptyRepetitionRuleB = one2n(ruleC);

    public MultipleEmptyRepetitionsGrammar() {
      ruleA.is(
          "foo",
          one2n("bar"),
          emptyRepetitionRuleA1,
          emptyRepetitionRuleA2
          );

      ruleB.is(emptyRepetitionRuleB);

      ruleC.is(opt("foobar"));
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void singleEmptyAlternativeTest() {
    SingleEmptyAlternativeGrammar grammar = new SingleEmptyAlternativeGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.rule)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher rule = getRuleMatcher(grammar.rule);
    assertThat(analyser.hasIssues(rule), is(true));
    assertThat(analyser.isLeftRecursive(rule), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(rule), is(false));
    assertThat(analyser.hasEmptyRepetitions(rule), is(false));
    assertThat(analyser.hasEmptyAlternatives(rule), is(true));
    assertThat(analyser.getEmptyAlternatives(rule), is((Set) Sets.newHashSet(new EmptyAlternative(grammar.orMatcher, grammar.emptyAlternative))));
  }

  private static class SingleEmptyAlternativeGrammar extends Grammar {

    public Rule rule;
    public Matcher emptyAlternative = opt("foo");
    public OrMatcher orMatcher = (OrMatcher) DelegatingMatcher.unwrap(or(emptyAlternative, "bar"));

    public SingleEmptyAlternativeGrammar() {
      rule.is(orMatcher);
    }

    @Override
    public Rule getRootRule() {
      return null;
    }

  }

  @Test
  public void multipleEmptyAlternativesTest() {
    MultipleEmptyAlternativesGrammar grammar = new MultipleEmptyAlternativesGrammar();

    GrammarAnalyser analyser = new GrammarAnalyser(grammar);
    assertThat(analyser.getRules(), is(getSet(grammar.ruleA, grammar.ruleB, grammar.ruleC)));
    assertThat(analyser.hasIssues(), is(true));

    RuleMatcher ruleA = getRuleMatcher(grammar.ruleA);
    assertThat(analyser.hasIssues(ruleA), is(true));
    assertThat(analyser.isLeftRecursive(ruleA), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleA), is(false));
    assertThat(analyser.hasEmptyRepetitions(ruleA), is(false));
    assertThat(analyser.hasEmptyAlternatives(ruleA), is(true));
    assertThat(analyser.getEmptyAlternatives(ruleA),
        is(
        (Set) Sets.newHashSet(
            new EmptyAlternative(grammar.orMatcherRuleA1, grammar.emptyAlternativeRuleA1),
            new EmptyAlternative(grammar.orMatcherRuleA2, grammar.emptyAlternativeRuleA2)
            )
        ));

    RuleMatcher ruleB = getRuleMatcher(grammar.ruleB);
    assertThat(analyser.hasIssues(ruleB), is(true));
    assertThat(analyser.isLeftRecursive(ruleB), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleB), is(false));
    assertThat(analyser.hasEmptyRepetitions(ruleB), is(false));
    assertThat(analyser.hasEmptyAlternatives(ruleB), is(true));
    assertThat(analyser.getEmptyAlternatives(ruleB), is((Set) Sets.newHashSet(new EmptyAlternative(grammar.orMatcherRuleB, getRuleMatcher(grammar.ruleC)))));

    RuleMatcher ruleC = getRuleMatcher(grammar.ruleC);
    assertThat(analyser.hasIssues(ruleC), is(false));
    assertThat(analyser.isLeftRecursive(ruleC), is(false));
    assertThat(analyser.isDependingOnLeftRecursiveRule(ruleC), is(false));
    assertThat(analyser.hasEmptyRepetitions(ruleC), is(false));
    assertThat(analyser.hasEmptyAlternatives(ruleC), is(false));
  }

  private static class MultipleEmptyAlternativesGrammar extends Grammar {

    public Rule ruleA;
    public Rule ruleB;
    public Rule ruleC;

    public Matcher emptyAlternativeRuleA1 = opt("hello");
    public OrMatcher orMatcherRuleA1 = (OrMatcher) DelegatingMatcher.unwrap(or("foo", emptyAlternativeRuleA1));
    public Matcher emptyAlternativeRuleA2 = opt("world");
    public OrMatcher orMatcherRuleA2 = (OrMatcher) DelegatingMatcher.unwrap(or("foo", emptyAlternativeRuleA2));

    public OrMatcher orMatcherRuleB = (OrMatcher) DelegatingMatcher.unwrap(or("foo", ruleC));

    public MultipleEmptyAlternativesGrammar() {
      ruleA.is(
          "foo",
          one2n("bar"),
          orMatcherRuleA1,
          orMatcherRuleA2
          );

      ruleB.is(orMatcherRuleB);

      ruleC.is(opt("foobar"));
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
