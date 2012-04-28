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
