/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.events.AutoCompleter;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.OrMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class OrAnalyser {
  
  public static final String PREFIX_EXAMPLE = "OrAnalyser.PREFIX_EXAMPLE";
  public static final int DEFAULT_MAX_TOKENS = 3;
  
  private final int maxTokens;
  
  private RuleMatcher currentRule;
  
  private LinkedList<Violation> emptyAlternativeViolations;
  private LinkedList<Violation> prefixAlternativeViolations;
  private LinkedList<Violation> potentialPrefixAlternativeViolations;
  
  public OrAnalyser() {
    this(DEFAULT_MAX_TOKENS);
  }
  
  public OrAnalyser(int maxTokens) {
    this.maxTokens = maxTokens;
  }
  
  private boolean tokenEquals(Token token1, Token token2) {
    if (!token1.getType().equals(token2.getType())) return false;
    
    return token1.getValue().equals(token2.getValue());
  }
  
  private boolean isNonEmptyPrefix(List<Token> list1, List<Token> list2) {
    if (list1.isEmpty()) return false;
    if (list1.size() > list2.size()) return false;
    
    for (int i = 0; i < list1.size(); i++) {
      if (!tokenEquals(list1.get(i), list2.get(i))) return false;
    }
    
    return true;
  }
  
  private boolean canMatchEmptyString(List<List<Token>> prefixes) {
    for (List<Token> prefix: prefixes) {
      if (prefix.isEmpty()) return true;
    }
    
    return false;
  }
  
  private class PrefixResult {
    
    public int i;
    public List<Token> prefix;
    
  }
  
  private PrefixResult getAnyNonEmptyPrefix(List<List<List<Token>>> allPrefixes, List<List<Token>> subjects) {
    for (List<Token> subject: subjects) {
      for (int i = 0; i < allPrefixes.size(); i++) {
        List<List<Token>> prefixes = allPrefixes.get(i);
        
        for (List<Token> prefix: prefixes) {
          if (isNonEmptyPrefix(prefix, subject)) {
            PrefixResult result = new PrefixResult();
            result.i = i;
            result.prefix = prefix;
            return result;
          }
        }
      }
    }
    
    return null;
  }

  private void analyse(Matcher matcher) {
    if (matcher instanceof OrMatcher) {
      /*
       * Common pitfalls:
       * 
       *  - One of the alternatives matches the empty string: Every alternative below it will be unreachable
       *  - A previous' alternative full match is prefix of the current's alternative full matches: The current alternative is (at least partially) unreachable
       *  - (if not in the case above) A previous' alternative full or partial match is prefix of the current's alternative full or partial match: The current alternative might be (at least partially) unreachable
       * 
       */
      
      List<List<List<Token>>> alternativesFullMatches = new LinkedList<List<List<Token>>>();
      List<List<List<Token>>> alternativesPartialMatches = new LinkedList<List<List<Token>>>();
      AutoCompleter autoCompleter = new AutoCompleter();
      for (Matcher alternative: matcher.children) {
        autoCompleter.autoComplete(alternative, maxTokens);
        List<List<Token>> fullMatches = autoCompleter.getFullMatches();
        List<List<Token>> partialMatches = autoCompleter.getPartialMatches();

        /* Can this alternative match the empty string? */
        if (canMatchEmptyString(fullMatches)) {
          emptyAlternativeViolations.add(new Violation(alternative, currentRule, ViolationSeverity.ERROR, matcher));
        }
        
        /* Is any previous alternative full matches prefix of the current full prefixes? */
        PrefixResult prefixResult = getAnyNonEmptyPrefix(alternativesFullMatches, fullMatches);
        if (prefixResult != null) {
          prefixAlternativeViolations.add(new Violation(alternative, currentRule, ViolationSeverity.ERROR, matcher, matcher.children[prefixResult.i]).addOrReplaceProperty(PREFIX_EXAMPLE, prefixResult.prefix));
        }
        
        /* (if not in the case above) Is any previous alternative full or partial matches prefix of the current full or partial prefixes? */
        if (prefixResult == null) {
          prefixResult = getAnyNonEmptyPrefix(alternativesFullMatches, partialMatches);
          if (prefixResult == null) {
            prefixResult = getAnyNonEmptyPrefix(alternativesPartialMatches, fullMatches);
          }
          if (prefixResult == null) {
            prefixResult = getAnyNonEmptyPrefix(alternativesPartialMatches, partialMatches);
          }
          
          if (prefixResult != null) {
            potentialPrefixAlternativeViolations.add(new Violation(alternative, currentRule, ViolationSeverity.WARNING, matcher, matcher.children[prefixResult.i]).addOrReplaceProperty(PREFIX_EXAMPLE, prefixResult.prefix));
          }
        }
        
        /* Add the prefixes to the structures */
        alternativesFullMatches.add(fullMatches);
        alternativesPartialMatches.add(partialMatches);
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
      currentRule = (RuleMatcher)rootMatcher;
    }
    
    analyse(rootMatcher);
    alreadyVisitedMatchers.add(rootMatcher);
    
    for (Matcher child: rootMatcher.children) {
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
  public LinkedList<Violation> getEmptyAlternativeViolations() {
    return emptyAlternativeViolations;
  }

  
  /**
   * @return the prefixAlternativeViolations
   */
  public LinkedList<Violation> getPrefixAlternativeViolations() {
    return prefixAlternativeViolations;
  }

  
  /**
   * @return the potentialPrefixAlternativeViolations
   */
  public LinkedList<Violation> getPotentialPrefixAlternativeViolations() {
    return potentialPrefixAlternativeViolations;
  }
  
}
