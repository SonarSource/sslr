/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.MemoizedMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class Profiler extends ParsingEventListener {

  public static class RuleCounter {

    private int hits = 0;
    private int matches = 0;
    private int backtracks = 0;
    private int memoizedHits = 0;
    private int memoizedMisses = 0;

    private long totalNonMemoizedHitsCpuTime = 0;

    private int maxLookahead = 0;
    private long totalLookaheads = 0;
    private int lookaheadsCounter = 0;

    private long maxBacktrackCpuTime = 0;
    private long totalBacktracksCpuTime = 0;
    private long backtracksCpuTimeCounter;

    private void hit() {
      hits += 1;
    }

    private void match() {
      matches += 1;
    }

    private void backtrack() {
      backtracks += 1;
    }

    private void memoizedHit() {
      memoizedHits += 1;
    }

    private void memoizedMiss() {
      memoizedMisses += 1;
    }

    private void addNonMemoizedHitCpuTime(long cpuTime) {
      totalNonMemoizedHitsCpuTime += cpuTime;
    }

    private void addLookahead(int lookahead) {
      if (lookahead > maxLookahead) {
        maxLookahead = lookahead;
      }
      totalLookaheads += lookahead;
      lookaheadsCounter++;
    }

    private void addBacktrackCpuTime(long cpuTime) {
      if (cpuTime > maxBacktrackCpuTime) {
        maxBacktrackCpuTime = cpuTime;
      }
      totalBacktracksCpuTime += cpuTime;
      backtracksCpuTimeCounter++;
    }

    public long getTotalNonMemoizedHitCpuTime() {
      return totalNonMemoizedHitsCpuTime;
    }

    public double getAverageLookahead() {
      return lookaheadsCounter == 0 ? 0 : totalLookaheads / (double) lookaheadsCounter;
    }

    public int getMaxLookahead() {
      return maxLookahead;
    }

    public double getAverageBacktracksCpuTime() {
      return backtracksCpuTimeCounter == 0 ? 0 : totalBacktracksCpuTime / (double) backtracksCpuTimeCounter;
    }

    public long getMaxBacktrackCpuTime() {
      return maxBacktrackCpuTime;
    }

    public int getHits() {
      return hits;
    }

    public int getMatches() {
      return matches;
    }

    public int getBacktracks() {
      return backtracks;
    }

    public int getMemoizedHits() {
      return memoizedHits;
    }

    public int getMemoizedMisses() {
      return memoizedMisses;
    }

  }

  private static class Timer {

    private boolean isTiming = false;
    private boolean isAborted = false;
    private long cpuTime = 0;
    private long start = 0L;

    private void start() {
      if (this.isAborted) {
        throw new IllegalStateException();
      }
      this.start = getCpuTime();
      this.isTiming = true;
    }

    private void stop() {
      if (!this.isTiming) {
        throw new IllegalStateException();
      }
      this.cpuTime += getCpuTime() - start;
      this.isTiming = false;
    }

    private void abort() {
      this.isTiming = false;
      this.isAborted = true;
    }

  }

  private static class Match {

    private int startIndex = -1;
    private boolean wasMemoized = false;
    private final Timer timer = new Timer();

  }

  public Map<RuleMatcher, RuleCounter> ruleStats;
  private Stack<Timer> timers;
  private Stack<Match> matches;
  private Timer lexerTimer;
  private Timer parserTimer;

  public Profiler() {
    initialize();
  }

  public void initialize() {
    ruleStats = new HashMap<RuleMatcher, RuleCounter>();
    timers = new Stack<Timer>();
    matches = new Stack<Match>();
    lexerTimer = new Timer();
    parserTimer = new Timer();
  }

  @Override
  public void beginLex() {
    lexerTimer.start();
  }

  @Override
  public void endLex() {
    lexerTimer.stop();
  }

  @Override
  public void beginParse() {
    parserTimer.start();
  }

  @Override
  public void endParse() {
    parserTimer.stop();
  }

  @Override
  public void enterRule(RuleMatcher rule, ParsingState parsingState) {
    getRuleCounter(rule).hit();
    startMatch(parsingState);
    startRecordingTime();
  }

  @Override
  public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
    stopMatch(rule, parsingState, false);
    stopRecordingTime(rule);
  }

  @Override
  public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
    stopMatch(rule, parsingState, true);
    stopRecordingTime(rule);
  }

  @Override
  public void memoizerHit(MemoizedMatcher matcher, ParsingState parsingState) {
    if (matcher instanceof RuleMatcher) {
      /* Match */
      matches.peek().wasMemoized = true;

      /* CPU time */
      abortRecordingTime();
      getRuleCounter((RuleMatcher) matcher).memoizedHit();
    }
  }

  @Override
  public void memoizerMiss(MemoizedMatcher matcher, ParsingState parsingState) {
    if (matcher instanceof RuleMatcher) {
      getRuleCounter((RuleMatcher) matcher).memoizedMiss();
    }
  }

  public long getLexerCpuTime() {
    return lexerTimer.cpuTime;
  }

  public long getParserCpuTime() {
    return parserTimer.cpuTime;
  }

  public long getHits() {
    long hits = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      hits += counter.hits;
    }

    return hits;
  }

  public long getBacktracks() {
    long backtracks = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      backtracks += counter.backtracks;
    }

    return backtracks;
  }

  public double getAverageBacktrackCpuTime() {
    double totalBacktracksCpuTime = 0;
    long backtracks = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      totalBacktracksCpuTime += counter.totalBacktracksCpuTime;
      backtracks += counter.backtracksCpuTimeCounter;
    }

    return backtracks == 0 ? 0 : totalBacktracksCpuTime / backtracks;
  }

  public long getMaxBacktrackCpuTime() {
    long maxBacktrackCpuTime = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      if (counter.getMaxBacktrackCpuTime() > maxBacktrackCpuTime) {
        maxBacktrackCpuTime = counter.getMaxBacktrackCpuTime();
      }
    }

    return maxBacktrackCpuTime;
  }

  public double getAverageLookahead() {
    double totalLookahead = 0;
    int lookaheadsCounter = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      totalLookahead += counter.totalLookaheads;
      lookaheadsCounter += counter.lookaheadsCounter;
    }

    return lookaheadsCounter == 0 ? 0 : totalLookahead / lookaheadsCounter;
  }

  public int getMaxLookahead() {
    int maxLookahead = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      if (counter.getMaxLookahead() > maxLookahead) {
        maxLookahead = counter.getMaxLookahead();
      }
    }

    return maxLookahead;
  }

  public long getMemoizedHits() {
    long memoizerHits = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      memoizerHits += counter.memoizedHits;
    }

    return memoizerHits;
  }

  public long getMemoizedMisses() {
    long memoizerMisses = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      memoizerMisses += counter.memoizedMisses;
    }

    return memoizerMisses;
  }

  public long getTotalNonMemoizedHitCpuTime() {
    long totalNonMemoizedHitCpuTime = 0;

    for (Map.Entry<RuleMatcher, RuleCounter> rule : ruleStats.entrySet()) {
      RuleCounter counter = rule.getValue();

      totalNonMemoizedHitCpuTime += counter.getTotalNonMemoizedHitCpuTime();
    }

    return totalNonMemoizedHitCpuTime;
  }

  private RuleCounter getRuleCounter(RuleMatcher rule) {
    RuleCounter counter = ruleStats.get(rule);
    if (counter == null) {
      counter = new RuleCounter();
      ruleStats.put(rule, counter);
    }

    return counter;
  }

  private void startMatch(ParsingState parsingState) {
    Match match = new Match();
    match.startIndex = parsingState.lexerIndex;
    match.wasMemoized = false;
    match.timer.start();
    matches.push(match);
  }

  private void stopMatch(RuleMatcher rule, ParsingState parsingState, boolean backtrack) {
    Match match = matches.pop();

    if (!match.wasMemoized) {
      RuleCounter counter = getRuleCounter(rule);

      if (!backtrack) {
        /* Match */
        counter.match();
      } else {
        /* Backtrack */
        match.timer.stop();
        counter.backtrack();

        /* Save the backtracking time */
        counter.addBacktrackCpuTime(match.timer.cpuTime);

        /* Compute the lookahead */
        int lookahead = parsingState.lexerIndex - match.startIndex + 1;
        counter.addLookahead(lookahead);
      }
    }
  }

  private void startRecordingTime() {
    /* Add the delta to the previous rule's timer */
    if (timers.size() > 0) {
      Timer oldTimer = timers.pop();
      if (!oldTimer.isAborted) {
        oldTimer.stop();
      }
      timers.push(oldTimer);
    }

    /* Start a timer for the current rule */
    Timer newTimer = new Timer();
    newTimer.start();
    timers.push(newTimer);
  }

  private void stopRecordingTime(RuleMatcher rule) {
    /* Handle the current timer */
    Timer currentTimer = timers.pop();
    if (!currentTimer.isAborted) {
      currentTimer.stop();
      getRuleCounter(rule).addNonMemoizedHitCpuTime(currentTimer.cpuTime);
    }

    /* Restart the previous rule's timer */
    if (timers.size() > 0) {
      Timer previousTimer = timers.pop();
      if (!previousTimer.isAborted) {
        previousTimer.start();
      }
      timers.push(previousTimer);
    }
  }

  private void abortRecordingTime() {
    timers.peek().abort();
  }

  private static long getCpuTime() {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
  }

  @Override
  public String toString() {
    PrintStream stream = null;

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      stream = new PrintStream(baos);
      ProfilerStream.print(this, stream);
      return baos.toString();
    } finally {
      if (stream != null) {
        stream.close();
      }
    }
  }

}
