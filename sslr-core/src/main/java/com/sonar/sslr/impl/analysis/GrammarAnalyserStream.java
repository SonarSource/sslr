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

public final class GrammarAnalyserStream {

  private GrammarAnalyserStream() {
  }

  public static void print(GrammarAnalyser analyser, PrintStream stream) {
    stream.println("Issues by rule:");
    stream.println("---------------");
    stream.println();

    for (RuleMatcher rule : analyser.getRules()) {
      if (analyser.hasIssues(rule)) {
        stream.println(rule.getName() + ": *** NOK ***");

        if (analyser.isSkipped(rule)) {
          Exception e = analyser.getSkippedCause(rule);

          stream.println("\tSkipped because of exception: \"" + e.toString() + "\"");
        } else if (analyser.isLeftRecursive(rule)) {
          LeftRecursionException e = analyser.getLeftRecursionException(rule);

          stream.println("\tThis rule is left recursive!");
          stream.println("\tStack trace:");
          stream.println(e.getRulesStackTrace());
        } else if (analyser.isDependingOnLeftRecursiveRule(rule)) {
          LeftRecursionException e = analyser.getLeftRecursionException(rule);

          stream.println("\tThis rule depends on the left recursive rule \"" + e.getLeftRecursiveRule().getName() + "\"");
        } else {
          if (analyser.hasEmptyRepetitions(rule)) {
            stream.println("\tThis rule contains the following empty repetitions, which lead to infinite loops:");
            for (OneToNMatcher matcher : analyser.getEmptyRepetitions(rule)) {
              stream.println("\t\t" + MatcherTreePrinter.print(matcher));
            }
            stream.println();
          }

          if (analyser.hasEmptyAlternatives(rule)) {
            stream.println("\tThis rule contains the following empty alternatives, which lead to dead grammar parts:");
            for (EmptyAlternative emptyAlternative : analyser.getEmptyAlternatives(rule)) {
              stream.println("\t\tAlternative " + MatcherTreePrinter.print(emptyAlternative.getAlternative()) + " in "
                + MatcherTreePrinter.print(emptyAlternative.getOrMatcher()));
            }
            stream.println();
          }
        }
      }
    }

    stream.println();
    stream.println("End of issues by rule");
    stream.println();
  }

}
