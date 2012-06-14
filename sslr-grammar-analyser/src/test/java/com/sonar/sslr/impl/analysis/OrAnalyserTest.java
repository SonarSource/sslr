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

import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

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

    assertHasViolations(orAnalyser, new Matcher[] { alt1 }, new Matcher[] {}, new Matcher[] {});
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] {}, new Matcher[] {});
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2 }, new Matcher[] {});
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2, alt3 }, new Matcher[] {});
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] {}, new Matcher[] {});
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2 }, new Matcher[] {});
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] {}, new Matcher[] { alt2 });
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

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2 }, new Matcher[] {});
  }

  @Test
  public void testCasePotentialSingleViolation() {
    OrAnalyser orAnalyser = new OrAnalyser();

    Matcher alt1 = opt("hello");
    Matcher alt2 = and("hello", "world");

    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
        );

    assertHasViolations(orAnalyser, new Matcher[] { alt1 }, new Matcher[] {}, new Matcher[] {});
  }

  @Test
  public void testCasePotentialSinglePrefixOneToken() {
    OrAnalyser orAnalyser = new OrAnalyser();

    Matcher alt1 = or("hello", "world");
    Matcher alt2 = or(and("hello", "un"), and("world", "deux"));

    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
        );

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2 }, new Matcher[] {});
  }

  @Test
  public void testCasePotentialSinglePrefixOneOrTwoTokens() {
    OrAnalyser orAnalyser = new OrAnalyser();

    Matcher alt1 = or("hello", and("world", "deux"));
    Matcher alt2 = or(and("hello", "un"), and("world", "deux"));

    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
        );

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2 }, new Matcher[] {});
  }

  @Test
  public void testCasePotentialSinglePrefixTwoTokens() {
    OrAnalyser orAnalyser = new OrAnalyser();

    Matcher alt1 = or(and("hello", "un"), and("world", "deux"));
    Matcher alt2 = or(and("hello", "un", "a"), and("world", "deux", "b"));

    orAnalyser.analyseMatcherTree(
        or(
            alt1,
            alt2
        )
        );

    assertHasViolations(orAnalyser, new Matcher[] {}, new Matcher[] { alt2 }, new Matcher[] {});
  }

  private void assertHasViolations(OrAnalyser orAnalyser, Matcher[] emptyAlternatives, Matcher[] prefixAlternatives,
      Matcher[] potentialPrefixAlternatives) {
    assertThat(orAnalyser.getEmptyAlternativeViolations().size(), is(emptyAlternatives.length));
    assertThat(orAnalyser.getPrefixAlternativeViolations().size(), is(prefixAlternatives.length));
    assertThat(orAnalyser.getPotentialPrefixAlternativeViolations().size(), is(potentialPrefixAlternatives.length));

    for (Matcher emptyAlternative : emptyAlternatives) {
      boolean found = false;
      for (Violation emptyViolation : orAnalyser.getEmptyAlternativeViolations()) {
        if (emptyViolation.getAffectedMatcher() == emptyAlternative) {
          found = true;
          break;
        }
      }
      if ( !found) {
        throw new AssertionError("Expected an empty violation for the matcher " + MatcherTreePrinter.print(emptyAlternative));
      }
    }

    for (Matcher prefixAlternative : prefixAlternatives) {
      boolean found = false;
      for (Violation prefixViolation : orAnalyser.getPrefixAlternativeViolations()) {
        if (prefixViolation.getAffectedMatcher() == prefixAlternative) {
          found = true;
          break;
        }
      }
      if ( !found) {
        throw new AssertionError("Expected a prefix violation for the matcher " + MatcherTreePrinter.print(prefixAlternative));
      }
    }

    for (Matcher potentialPrefixAlternative : potentialPrefixAlternatives) {
      boolean found = false;
      for (Violation potentialPrefixViolation : orAnalyser.getPotentialPrefixAlternativeViolations()) {
        if (potentialPrefixViolation.getAffectedMatcher() == potentialPrefixAlternative) {
          found = true;
          break;
        }
      }
      if ( !found) {
        throw new AssertionError("Expected a potential prefix violation for the matcher "
            + MatcherTreePrinter.print(potentialPrefixAlternative));
      }
    }

  }

}
