/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.matcher.OneToNMatcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.*;
import static com.sonar.sslr.impl.analysis.FirstVisitor.*;

public class GrammarAnalyser {

  private final Set<RuleMatcher> rules;
  private final Map<RuleMatcher, UnsupportedMatcherException> skippedRules = Maps.newHashMap();
  private final Map<RuleMatcher, LeftRecursionException> dependOnLeftRecursiveRules = Maps.newHashMap();
  private final Map<RuleMatcher, LeftRecursionException> leftRecursiveRules = Maps.newHashMap();
  private final Map<RuleMatcher, Set<OneToNMatcher>> emptyRepetitions = Maps.newHashMap();
  private final Map<RuleMatcher, Set<EmptyAlternative>> emptyAlternatives = Maps.newHashMap();

  public GrammarAnalyser(Grammar grammar) {
    rules = getRuleMatchers(grammar);

    for (RuleMatcher rule : rules) {
      detectIssues(rule);
    }
  }

  public Set<RuleMatcher> getRules() {
    return Collections.unmodifiableSet(rules);
  }

  public boolean isLeftRecursive(RuleMatcher rule) {
    return leftRecursiveRules.containsKey(rule);
  }

  public boolean isDependingOnLeftRecursiveRule(RuleMatcher rule) {
    return dependOnLeftRecursiveRules.containsKey(rule);
  }

  public LeftRecursionException getLeftRecursionException(RuleMatcher rule) {
    LeftRecursionException e = dependOnLeftRecursiveRules.get(rule);
    if (e == null) {
      e = leftRecursiveRules.get(rule);
    }
    checkArgument(e != null, "The given rule \"" + rule.getName() + "\" has no associated left recursion exception");

    return e;
  }

  public boolean hasEmptyRepetitions(RuleMatcher rule) {
    return emptyRepetitions.containsKey(rule);
  }

  public Set<OneToNMatcher> getEmptyRepetitions(RuleMatcher rule) {
    checkArgument(hasEmptyRepetitions(rule), "The given rule \"" + rule.getName() + "\" has no empty repetitions");
    return emptyRepetitions.get(rule);
  }

  public boolean hasEmptyAlternatives(RuleMatcher rule) {
    return emptyAlternatives.containsKey(rule);
  }

  public Set<EmptyAlternative> getEmptyAlternatives(RuleMatcher rule) {
    checkArgument(hasEmptyAlternatives(rule), "The given rule \"" + rule.getName() + "\" has no empty alternatives");
    return emptyAlternatives.get(rule);
  }

  public boolean isSkipped(RuleMatcher rule) {
    return skippedRules.containsKey(rule);
  }

  public UnsupportedMatcherException getSkippedCause(RuleMatcher rule) {
    checkArgument(isSkipped(rule), "The given rule \"" + rule.getName() + "\" has not skipped");
    return skippedRules.get(rule);
  }

  public boolean hasIssues() {
    return !skippedRules.isEmpty() || !dependOnLeftRecursiveRules.isEmpty() || !leftRecursiveRules.isEmpty() || !emptyRepetitions.isEmpty() || !emptyAlternatives.isEmpty();
  }

  public boolean hasIssues(RuleMatcher rule) {
    return isSkipped(rule) || isLeftRecursive(rule) || isDependingOnLeftRecursiveRule(rule) || hasEmptyRepetitions(rule) || hasEmptyAlternatives(rule);
  }

  private Set<RuleMatcher> getRuleMatchers(Grammar grammar) {
    try {
      Set<RuleMatcher> ruleMatchers = Sets.newHashSet();

      for (Field ruleField : grammar.getAllRuleFields(grammar.getClass())) {
        RuleDefinition rule = (RuleDefinition) ruleField.get(grammar);
        ruleMatchers.add(rule.getRule());
      }

      return ruleMatchers;
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void detectIssues(RuleMatcher rule) {
    try {
      first(rule); // Cause the LeftRecursionExceptions

      EmptyRepetitionVisitor emptyRepetitionVisitor = new EmptyRepetitionVisitor();
      emptyRepetitionVisitor.visit(rule);
      if (!emptyRepetitionVisitor.getEmptyRepetitions().isEmpty()) {
        emptyRepetitions.put(rule, emptyRepetitionVisitor.getEmptyRepetitions());
      }

      EmptyAlternativeVisitor emptyAlternativeVisitor = new EmptyAlternativeVisitor();
      emptyAlternativeVisitor.visit(rule);
      if (!emptyAlternativeVisitor.getEmptyAlternatives().isEmpty()) {
        emptyAlternatives.put(rule, emptyAlternativeVisitor.getEmptyAlternatives());
      }
    } catch (UnsupportedMatcherException e) {
      skippedRules.put(rule, e);
    } catch (LeftRecursionException e) {
      if (rule.equals(e.getLeftRecursiveRule())) {
        leftRecursiveRules.put(rule, e);
      } else {
        dependOnLeftRecursiveRules.put(rule, e);
      }
    }
  }

}
