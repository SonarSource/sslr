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

import static com.google.common.base.Preconditions.checkArgument;
import static com.sonar.sslr.impl.analysis.FirstVisitor.first;

public class GrammarAnalyser {

  private final Set<RuleMatcher> rules;
  private final Map<RuleMatcher, Exception> skippedRules = Maps.newHashMap();
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

  public Exception getSkippedCause(RuleMatcher rule) {
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

      for (Field ruleField : Grammar.getAllRuleFields(grammar.getClass())) {
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
      // Causes the LeftRecursionException
      first(rule);

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
    } catch (LeftRecursionException e) {
      if (rule.equals(e.getLeftRecursiveRule())) {
        leftRecursiveRules.put(rule, e);
      } else {
        dependOnLeftRecursiveRules.put(rule, e);
      }
    } catch (Exception e) {
      skippedRules.put(rule, e);
    }
  }

}
