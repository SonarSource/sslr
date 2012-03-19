/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.OneToNMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.io.PrintStream;

public class GrammarAnalyserStream {

  private GrammarAnalyserStream() {
  }

  public static void print(GrammarAnalyser analyser, PrintStream stream) {
    System.out.println("Issues by rule:");
    System.out.println("---------------");
    System.out.println();

    for (RuleMatcher rule : analyser.getRules()) {
      if (analyser.hasIssues(rule)) {
        System.out.println(rule.getName() + ": *** NOK ***");

        if (analyser.isSkipped(rule)) {
          Exception e = analyser.getSkippedCause(rule);

          System.out.println("\tSkipped because of exception: \"" + e.toString() + "\"");
        } else if (analyser.isLeftRecursive(rule)) {
          LeftRecursionException e = analyser.getLeftRecursionException(rule);

          System.out.println("\tThis rule is left recursive!");
          System.out.println("\tStack trace:");
          System.out.println(e.getRulesStackTrace());
        } else if (analyser.isDependingOnLeftRecursiveRule(rule)) {
          LeftRecursionException e = analyser.getLeftRecursionException(rule);

          System.out.println("\tThis rule depends on the left recursive rule \"" + e.getLeftRecursiveRule().getName() + "\"");
        } else {
          if (analyser.hasEmptyRepetitions(rule)) {
            System.out.println("\tThis rule contains the following empty repetitions, which lead to infinite loops:");
            for (OneToNMatcher matcher : analyser.getEmptyRepetitions(rule)) {
              System.out.println("\t\t" + MatcherTreePrinter.print(matcher));
            }
            System.out.println();
          }

          if (analyser.hasEmptyRepetitions(rule)) {
            System.out.println("\tThis rule contains the following empty alternatives, which lead to dead grammar parts:");
            for (EmptyAlternative emptyAlternative : analyser.getEmptyAlternatives(rule)) {
              System.out.println("\t\tAlternative " + MatcherTreePrinter.print(emptyAlternative.getAlternative()) + " in "
                + MatcherTreePrinter.print(emptyAlternative.getOrMatcher()));
            }
            System.out.println();
          }
        }
      }
    }

    System.out.println();
    System.out.println("End of issues by rule");
    System.out.println();
  }

}
