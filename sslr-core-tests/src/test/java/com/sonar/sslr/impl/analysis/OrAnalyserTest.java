/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;

public class OrAnalyserTest {
  
  @Test
  public void testCaseEmpty() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = opt("hey");
    Matcher alt2 = and("haha");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{ alt1 }, new Matcher[]{}, new Matcher[]{});
  }
  
  @Test
  public void testCaseCorrectedEmpty() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("hey");
    Matcher alt2 = and("haha");
    
    orAnalyser.analyseMatcherTree(
        opt(
            or(
                alt1,
                alt2
            )
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{}, new Matcher[]{});
  }
  
  @Test
  public void testCasePrefix() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("hello");
    Matcher alt2 = and("hello", "world");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{ alt2 }, new Matcher[]{});
  }
  
  @Test
  public void testCaseMultiplePrefixes() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("hello");
    Matcher alt2 = and("hello", "world");
    Matcher alt3 = and("hello", "folks");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2,
            alt3
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{ alt2, alt3 }, new Matcher[]{});
  }
  
  @Test
  public void testCaseCorrectOrderedPrefix() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("hello", "world");
    Matcher alt2 = and("hello");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{}, new Matcher[]{});
  }
  
  @Test
  public void testCasePotentialPrefixMinimalCase() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("1", "2", "3", "4", "5");
    Matcher alt2 = and("1", "2", "3", "4", "5", "hoho");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{}, new Matcher[]{ alt2 });
  }
  
  @Test
  public void testCasePotentialPrefix() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("1", "2", "3", "4", "5", "hehe");
    Matcher alt2 = and("1", "2", "3", "4", "5", "hoho");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{}, new Matcher[]{ alt2 });
  }
  
  @Test
  public void testCasePotentialFalsePositive() {
    OrAnalyser orAnalyser = new OrAnalyser();
    
    Matcher alt1 = and("hello");
    Matcher alt2 = and(o2n("hello"), "hello");
    
    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
    );
    
    assertHasViolations(orAnalyser, new Matcher[]{}, new Matcher[]{}, new Matcher[]{ alt2 });
  }
  
  private void assertHasViolations(OrAnalyser orAnalyser, Matcher[] emptyAlternatives, Matcher[] prefixAlternatives, Matcher[] potentialPrefixAlternatives) {
    assertThat(orAnalyser.getEmptyAlternativeViolations().size(), is(emptyAlternatives.length));
    assertThat(orAnalyser.getPrefixAlternativeViolations().size(), is(prefixAlternatives.length));
    assertThat(orAnalyser.getPotentialPrefixAlternativeViolations().size(), is(potentialPrefixAlternatives.length));
    
    for (Matcher emptyAlternative: emptyAlternatives) {
      boolean found = false;
      for (Violation emptyViolation: orAnalyser.getEmptyAlternativeViolations()) {
        if (emptyViolation.getAffectedMatcher() == emptyAlternative) {
          found = true;
          break;
        }
      }
      if (!found) {
        throw new AssertionError("Expected an empty violation for the matcher " + MatcherTreePrinter.print(emptyAlternative));
      }
    }
    
    for (Matcher prefixAlternative: prefixAlternatives) {
      boolean found = false;
      for (Violation prefixViolation: orAnalyser.getPrefixAlternativeViolations()) {
        if (prefixViolation.getAffectedMatcher() == prefixAlternative) {
          found = true;
          break;
        }
      }
      if (!found) {
        throw new AssertionError("Expected an prefix violation for the matcher " + MatcherTreePrinter.print(prefixAlternative));
      }
    }
    
    for (Matcher potentialPrefixAlternative: potentialPrefixAlternatives) {
      boolean found = false;
      for (Violation potentialPrefixViolation: orAnalyser.getPotentialPrefixAlternativeViolations()) {
        if (potentialPrefixViolation.getAffectedMatcher() == potentialPrefixAlternative) {
          found = true;
          break;
        }
      }
      if (!found) {
        throw new AssertionError("Expected an potential prefix violation for the matcher " + MatcherTreePrinter.print(potentialPrefixAlternative));
      }
    }
    
    
  }
  
}
