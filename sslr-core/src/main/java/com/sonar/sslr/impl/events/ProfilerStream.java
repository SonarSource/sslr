/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

import com.sonar.sslr.impl.events.Profiler.RuleCounter;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class ProfilerStream {

  private ProfilerStream() {
  }

  public static long nsToMs(long ns) {
    return ns / 1000000L;
  }

  public static double nsToMs(double ns) {
    return ns / 1000000L;
  }

  private static <T> List<T> asSortedList(Collection<T> c, Comparator<T> cmp) {
    List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list, cmp);
    return list;
  }

  public static void print(Profiler profiler, PrintStream stream) {
    List<Map.Entry<RuleMatcher, RuleCounter>> sortedRules = asSortedList(profiler.ruleStats.entrySet(),
        new Comparator<Map.Entry<RuleMatcher, RuleCounter>>() {

          public int compare(Entry<RuleMatcher, RuleCounter> o1, Entry<RuleMatcher, RuleCounter> o2) {
            if (o1.getValue().getTotalNonMemoizedHitCpuTime() == o2.getValue().getTotalNonMemoizedHitCpuTime()) {
              return 0;
            }
            return o1.getValue().getTotalNonMemoizedHitCpuTime() > o2.getValue().getTotalNonMemoizedHitCpuTime() ? -1 : 1;
          }
        });

    stream.println("Lexer CPU time: " + nsToMs(profiler.getLexerCpuTime()) + "ms");
    stream.println("Parser CPU time: " + nsToMs(profiler.getParserCpuTime()) + "ms");
    stream.println("How many distinct rules were hit: " + profiler.ruleStats.size());
    stream.println("How many times rules were hit: " + profiler.getHits());
    stream.println("How many backtracks: " + profiler.getBacktracks() + " (time avg: " + nsToMs(profiler.getAverageBacktrackCpuTime())
        + "ms, max: " + nsToMs(profiler.getMaxBacktrackCpuTime()) + "ms, lookahead avg: "
        + String.format("%.2f", profiler.getAverageLookahead()) + ", max: " + profiler.getMaxLookahead() + ")");
    stream.println("Memoizer: " + profiler.getMemoizedHits() + " hits, " + profiler.getMemoizedMisses() + " misses");
    stream.println("Total non memoized hits CPU Time: " + nsToMs(profiler.getTotalNonMemoizedHitCpuTime()) + "ms");
    stream.println();
    stream.println("Rule statistics:");
    for (Map.Entry<RuleMatcher, RuleCounter> rule : sortedRules) {
      RuleCounter counter = rule.getValue();

      stream.print(String.format(" - %-27s", rule.getKey()) + " ");
      stream.print("Hits: " + String.format("%6d", counter.getHits()) + "       ");
      stream.print("Backtracks: " + String.format("%6d", counter.getBacktracks()) + " (time avg: "
          + String.format("%8.3f", nsToMs(counter.getAverageBacktracksCpuTime())) + "ms, max: "
          + String.format("%5d", nsToMs(counter.getMaxBacktrackCpuTime())) + "ms, lookahead avg: "
          + String.format("%6.2f", counter.getAverageLookahead()) + ", max: " + String.format("%3d", counter.getMaxLookahead()) + ")"
          + "       ");
      stream.print("Memoizer: " + String.format("%6d", counter.getMemoizedHits()) + " hits, "
          + String.format("%6d", counter.getMemoizedMisses()) + " misses" + "       ");
      stream.print("Total CPU Time: " + String.format("%6d", nsToMs(counter.getTotalNonMemoizedHitCpuTime())) + "ms");
      stream.println();
    }
  }

}
