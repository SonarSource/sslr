/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

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
      if (analyser.isLeftRecursive(rule)) {
        LeftRecursionException e = analyser.getLeftRecursionxception(rule);

        System.out.println(rule.getName() + ": *** NOK, contains a left recursion ***");
        System.out.println(e.getRulesStackTrace());
      } else if (analyser.isDependingOnLeftRecursiveRule(rule)) {
        LeftRecursionException e = analyser.getLeftRecursionxception(rule);

        System.out.println(rule.getName() + ": *** NOK, dependency on left recursive rule " + e.getLeftRecursiveRule().getName() + " ***");
      }
    }

    System.out.println();
    System.out.println("End of issues by rule");
    System.out.println();
  }

}
