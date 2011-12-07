/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.events.AutoCompleter;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.OrMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class OrAnalyser {

  public static final String PREFIX_EXAMPLE = "OrAnalyser.PREFIX_EXAMPLE";
  public static final int DEFAULT_MAX_TOKENS = 5;

  private final int maxTokens;

  private RuleMatcher currentRule;

  private List<Violation> emptyAlternativeViolations;
  private List<Violation> prefixAlternativeViolations;
  private List<Violation> potentialPrefixAlternativeViolations;

  public OrAnalyser() {
    this(DEFAULT_MAX_TOKENS);
  }

  public OrAnalyser(int maxTokens) {
    this.maxTokens = maxTokens;
  }

  private boolean tokenEquals(Token token1, Token token2) {
    if ( !token1.getType().equals(token2.getType())) {
      return false;
    }

    return token1.getValue().equals(token2.getValue());
  }

  private boolean isPrefix(List<Token> prefix, List<Token> subject) {
    if (prefix.size() > subject.size()) {
      return false;
    }

    for (int i = 0; i < prefix.size(); i++) {
      if ( !tokenEquals(prefix.get(i), subject.get(i))) {
        return false;
      }
    }

    return true;
  }

  private boolean canMatchEmptyString(List<List<Token>> prefixes) {
    for (List<Token> prefix : prefixes) {
      if (prefix.isEmpty()) {
        return true;
      }
    }

    return false;
  }

  private List<Token> getOnePrefix(List<List<Token>> prefixes, List<List<Token>> subjects) {
    for (List<Token> subject : subjects) {
      for (List<Token> prefix : prefixes) {
        if (isPrefix(prefix, subject)) {
          return prefix;
        }
      }
    }

    return null;
  }

  private void handleEmptyPrefixes(OrMatcher orMatcher, ListMultimap<Integer, List<Token>> fullMatches,
      ListMultimap<Integer, List<Token>> partialMatches) {
    for (int alternativeIndex = 0; alternativeIndex < orMatcher.children.length; alternativeIndex++) {
      List<List<Token>> prefixes = fullMatches.get(alternativeIndex);
      if (prefixes == null) {
        continue;
      }

      if (canMatchEmptyString(prefixes)) {
        emptyAlternativeViolations
            .add(new Violation(orMatcher.children[alternativeIndex], currentRule, ViolationConfidence.SURE, orMatcher));
        fullMatches.removeAll(alternativeIndex);
        partialMatches.removeAll(alternativeIndex);
      }
    }
  }

  private void handlePrefixes(OrMatcher orMatcher, ListMultimap<Integer, List<Token>> prefixesForAllAlternatives,
      ListMultimap<Integer, List<Token>> subjectsForAllAlternatives, List<Violation> violationsList,
      ViolationConfidence violationConfidence, ListMultimap<Integer, List<Token>> otherPrefixesToDeleteFrom) {
    for (int alternativeIndex = 0; alternativeIndex < orMatcher.children.length; alternativeIndex++) {
      List<List<Token>> subjects = subjectsForAllAlternatives.get(alternativeIndex);
      if (subjects == null) {
        continue;
      }

      for (int prefixingAlternativeIndex = 0; prefixingAlternativeIndex < alternativeIndex; prefixingAlternativeIndex++) {
        List<List<Token>> prefixes = prefixesForAllAlternatives.get(prefixingAlternativeIndex);
        if (prefixes == null) {
          continue;
        }

        List<Token> prefixExample = getOnePrefix(prefixes, subjects);
        if (prefixExample != null) {
          violationsList.add(new Violation(orMatcher.children[alternativeIndex], currentRule, violationConfidence, orMatcher,
              orMatcher.children[prefixingAlternativeIndex]).addOrReplaceProperty(PREFIX_EXAMPLE, prefixExample));
          prefixesForAllAlternatives.removeAll(alternativeIndex);
          if (prefixesForAllAlternatives != subjectsForAllAlternatives) {
            subjectsForAllAlternatives.removeAll(alternativeIndex);
          }
          if (otherPrefixesToDeleteFrom != null) {
            otherPrefixesToDeleteFrom.removeAll(alternativeIndex);
          }
          break;
        }
      }
    }
  }

  private void handlePartialPrefixes(OrMatcher orMatcher, ListMultimap<Integer, List<Token>> partialMatches,
      ListMultimap<Integer, List<Token>> alternativesPrefixes) {
    ListMultimap<Integer, Integer> alreadyAdded = LinkedListMultimap.create();

    for (int prefixingAlternativeIndex = 0; prefixingAlternativeIndex < orMatcher.children.length; prefixingAlternativeIndex++) {
      List<List<Token>> prefixes = partialMatches.get(prefixingAlternativeIndex);
      if (prefixes == null) {
        continue;
      }

      for (List<Token> prefix : prefixes) {
        boolean found = false;

        for (int subjectAlternativeIndex = prefixingAlternativeIndex + 1; !found && subjectAlternativeIndex < orMatcher.children.length; subjectAlternativeIndex++) {
          List<List<Token>> subjects = partialMatches.get(subjectAlternativeIndex);
          if (subjects == null) {
            continue;
          }

          int prefixIndex = 0;
          for (List<Token> subject : subjects) {
            if (isPrefix(prefix, subject)) {
              /* We should keep both the prefix and the subject! */
              alternativesPrefixes.put(prefixingAlternativeIndex, prefix);
              if (alreadyAdded.get(subjectAlternativeIndex) != null && !alreadyAdded.get(subjectAlternativeIndex).contains(prefixIndex)) {
                alreadyAdded.put(subjectAlternativeIndex, prefixIndex);
                alternativesPrefixes.put(subjectAlternativeIndex, subject);
              }
              found = true;
              break;
            }
            prefixIndex++;
          }
        }
      }
    }
  }

  private void analyse(Matcher matcher) {
    if (matcher instanceof OrMatcher) {
      /*
       * Common pitfalls:
       * 
       * - One of the alternatives matches the empty string: Every alternative below it will be unreachable - A previous' alternative full
       * match is prefix of the current's alternative full matches: The current alternative is (at least partially) unreachable - (if not in
       * the case above) A previous' alternative full or partial match is prefix of the current's alternative full or partial match: The
       * current alternative might be (at least partially) unreachable
       */

      ListMultimap<Integer, List<Token>> alternativesPrefixes = LinkedListMultimap.create();

      /* Generate the first iteration prefixes, an empty one for each alternative (every alternative is to be explored) */
      for (int alternativeIndex = 0; alternativeIndex < matcher.children.length; alternativeIndex++) {
        alternativesPrefixes.put(alternativeIndex, new LinkedList<Token>());
      }

      /* Iterative auto completion (one token at a time) */
      AutoCompleter autoCompleter = new AutoCompleter();
      for (int tokens = 0; tokens < maxTokens && !alternativesPrefixes.isEmpty(); tokens++) {
        ListMultimap<Integer, List<Token>> fullMatches = LinkedListMultimap.create();
        ListMultimap<Integer, List<Token>> partialMatches = LinkedListMultimap.create();

        /* Auto complete */
        for (Map.Entry<Integer, List<Token>> entry : alternativesPrefixes.entries()) {
          autoCompleter.autoComplete(matcher.children[entry.getKey()], entry.getValue(), 1);

          fullMatches.putAll(entry.getKey(), autoCompleter.getFullMatches());
          partialMatches.putAll(entry.getKey(), autoCompleter.getPartialMatches());
        }

        /* Handle the empty prefixes */
        handleEmptyPrefixes((OrMatcher) matcher, fullMatches, partialMatches);

        /* Handle the Full versus Full violations */
        handlePrefixes((OrMatcher) matcher, fullMatches, fullMatches, prefixAlternativeViolations, ViolationConfidence.SURE, partialMatches);

        /* Handle the Full versus Partial violations */
        handlePrefixes((OrMatcher) matcher, fullMatches, partialMatches, prefixAlternativeViolations, ViolationConfidence.HIGH, null);

        /*
         * Handle the Partial versus Full violations: Nothing to do, since those do not generate any violation actually (partial needs at
         * least 1 more token to be complete, and hence cannot be prefix of the same length'ed full)
         */

        /* Put the Partial versus Partial prefixes in alternativesPrefixes for the next auto completion round */
        alternativesPrefixes.clear();
        handlePartialPrefixes((OrMatcher) matcher, partialMatches, alternativesPrefixes);
      }

      if ( !alternativesPrefixes.isEmpty()) {
        /* Handle the remaining prefixes as low violations, as we were unable to better classify them */
        handlePrefixes((OrMatcher) matcher, alternativesPrefixes, alternativesPrefixes, potentialPrefixAlternativeViolations,
            ViolationConfidence.LOW, null);
      }
    }
  }

  public void analyseMatcherTree(Matcher rootMatcher) {
    emptyAlternativeViolations = new LinkedList<Violation>();
    prefixAlternativeViolations = new LinkedList<Violation>();
    potentialPrefixAlternativeViolations = new LinkedList<Violation>();

    analyseMatcherTree(rootMatcher, new HashSet<Matcher>());
  }

  private void analyseMatcherTree(Matcher rootMatcher, HashSet<Matcher> alreadyVisitedMatchers) {
    RuleMatcher previousRule = null;
    if (rootMatcher instanceof RuleMatcher) {
      previousRule = currentRule;
      currentRule = (RuleMatcher) rootMatcher;
    }

    analyse(rootMatcher);
    alreadyVisitedMatchers.add(rootMatcher);

    for (Matcher child : rootMatcher.children) {
      if (alreadyVisitedMatchers.add(child)) {
        /* This child is not yet visited (was successfully added to the set) */
        analyseMatcherTree(child, alreadyVisitedMatchers);
      }
    }

    if (rootMatcher instanceof RuleMatcher) {
      currentRule = previousRule;
    }
  }

  /**
   * @return the emptyAlternativeViolations
   */
  public List<Violation> getEmptyAlternativeViolations() {
    return emptyAlternativeViolations;
  }

  /**
   * @return the prefixAlternativeViolations
   */
  public List<Violation> getPrefixAlternativeViolations() {
    return prefixAlternativeViolations;
  }

  /**
   * @return the potentialPrefixAlternativeViolations
   */
  public List<Violation> getPotentialPrefixAlternativeViolations() {
    return potentialPrefixAlternativeViolations;
  }

}
