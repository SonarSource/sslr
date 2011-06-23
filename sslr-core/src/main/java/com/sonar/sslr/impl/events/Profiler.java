/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.lang.management.*;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class Profiler extends ParsingEventListener {
	
	private class RuleCounter {
		
		 private int hits = 0;
		 private int matches = 0;
		 private int backtracks = 0;
		 private int memoizedHits = 0;
		 private int memoizedMisses = 0;
		 private LinkedList<Long> nonMemoizedHitsCpuTime = new LinkedList<Long>();
		 private LinkedList<Integer> lookaheads = new LinkedList<Integer>();
		 private LinkedList<Long> backtracksCpuTime = new LinkedList<Long>();
		 
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
			 nonMemoizedHitsCpuTime.add(cpuTime);
		 }
		 
		 private void addLookahead(int lookahead) {
			 lookaheads.add(lookahead);
		 }
		 
		 private void addBacktrackCpuTime(long cpuTime) {
			 backtracksCpuTime.add(cpuTime);
		 }
		 
		 private long getTotalNonMemoizedHitCpuTime() {
			 return sum(nonMemoizedHitsCpuTime);
		 }
		 
		 private double getAverageLookahead() {
			 return average(lookaheads);
		 }
		 
		 private long getMaxLookahead() {
			 return max(lookaheads);
		 }
		 
		 private double getAverageBacktracksCpuTime() {
			 return average(backtracksCpuTime);
		 }
		 
		 private long getMaxBacktrackCpuTime() {
			 return max(backtracksCpuTime);
		 }
		 
	}
	
	private class Timer {
		
		private boolean isTiming = false;
		private boolean isAborted = false;
		private long cpuTime = 0;
		private long start = 0L;
		
		private void start() {
			if (this.isAborted) throw new IllegalStateException();
			this.start = getCpuTime();
			this.isTiming = true;
		}
		
		private void stop() {
			if (!this.isTiming) throw new IllegalStateException();
			this.cpuTime += getCpuTime() - start;
			this.isTiming = false;
		}
		
		private void abort() {
			this.isTiming = false;
			this.isAborted = true;
		}
		
	}
	
	private class Match {
		
		private int startIndex = -1;
		private boolean wasMemoized = false;
		private Timer timer = new Timer();
		
	}
	
	public HashMap<RuleMatcher, RuleCounter> ruleStats;
	private Stack<Timer> timers;
	private Stack<Match> matches;
	private Timer lexerTimer;
	private Timer parserTimer;
	
	public Profiler() {
		initialize();
	}

	private void initialize() {
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
		startMatch(rule, parsingState);
		startRecordingTime(rule);
	}

	@Override
	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
		stopMatch(rule, parsingState, false);
		stopRecordingTime(rule);
	}

	@Override
	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState, BacktrackingException re) {
		stopMatch(rule, parsingState, true);
		stopRecordingTime(rule);
	}
	
	@Override
	public void memoizerHit(Matcher matcher, ParsingState parsingState) {
		if (matcher instanceof RuleMatcher) {
			/* Match */
			matches.peek().wasMemoized = true;
			
			/* CPU time */
			abortRecordingTime((RuleMatcher)matcher);
			getRuleCounter((RuleMatcher)matcher).memoizedHit();
		}
	}
	
	@Override
	public void memoizerMiss(Matcher matcher, ParsingState parsingState) {
		if (matcher instanceof RuleMatcher) {
			getRuleCounter((RuleMatcher)matcher).memoizedMiss();
		}
	}
	
	public void printProfiler(PrintStream stream) {
		stream.println("Lexer CPU time: " + nsToMs(lexerTimer.cpuTime) + "ms");
		stream.println("Parser CPU time: " + nsToMs(parserTimer.cpuTime) + "ms");
		stream.println("How many distinct rules were hit: " + ruleStats.size());
		stream.println("How many times rules were hit: " + getHits());
		stream.println("How many backtracks: " + getBacktracks() + " (time avg: " + nsToMs(getAverageBacktrackCpuTime()) + "ms, max: " + nsToMs(getMaxBacktrackCpuTime()) + "ms, lookahead avg: " + String.format("%.2f", getAverageLookahead()) + ", max: " + getMaxLookahead() + ")");
		stream.println("Memoizer: " + getMemoizedHits() + " hits, " + getMemoizedMisses() + " misses");
		stream.println("Total non memoized hits CPU Time: " + nsToMs(getTotalNonMemoizedHitCpuTime()) + "ms");
		stream.println();
		stream.println("Rule statistics:");
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();

			stream.print(String.format(" - %-25s", rule.getKey()));
			stream.print("Hits: " + String.format("%4d", counter.hits) + "       ");
			stream.print("Backtracks: " + String.format("%4d", counter.backtracks) + " (time avg: " + String.format("%8.3f", nsToMs(counter.getAverageBacktracksCpuTime())) + "ms, max: " + String.format("%5d", nsToMs(counter.getMaxBacktrackCpuTime())) + "ms, lookahead avg: " + String.format("%6.2f", counter.getAverageLookahead()) + ", max: " + String.format("%3d", counter.getMaxLookahead()) + ")" + "       ");
			stream.print("Memoizer: " + String.format("%4d", counter.memoizedHits) + " hits, " + String.format("%4d", counter.memoizedMisses) + " misses"  + "       ");
			stream.print("Total CPU Time: " + String.format("%6d", nsToMs(counter.getTotalNonMemoizedHitCpuTime())) + "ms" + "       ");
			stream.println();
		}
	}
	
	private long getHits() {
		long hits = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			hits += counter.hits;
		}
		
		return hits;
	}
	
	private long getBacktracks() {
		long backtracks = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			backtracks += counter.backtracks;
		}
		
		return backtracks;
	}
	
	private double getAverageBacktrackCpuTime() {
		double totalBacktracksCpuTime = 0;
		int backtracks = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			for (Long backtrackCpuTime: counter.backtracksCpuTime) {
				totalBacktracksCpuTime += backtrackCpuTime;
				backtracks++;
			}
		}
		
		return (backtracks == 0) ? 0 : totalBacktracksCpuTime / backtracks;
	}
	
	private long getMaxBacktrackCpuTime() {
		long maxBacktrackCpuTime = 0;

		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			for (Long backtrackCpuTime: counter.backtracksCpuTime) {
				if (backtrackCpuTime > maxBacktrackCpuTime) maxBacktrackCpuTime = backtrackCpuTime;
			}
		}
		
		return maxBacktrackCpuTime;
	}
	
	private double getAverageLookahead() {
		double totalLookahead = 0;
		int lookaheads = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			for (Integer lookahead: counter.lookaheads) {
				totalLookahead += lookahead;
				lookaheads++;
			}
		}
		
		return (lookaheads == 0) ? 0 : totalLookahead / lookaheads;
	}
	
	private int getMaxLookahead() {
		int maxLookahead = 0;

		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			for (Integer lookahead: counter.lookaheads) {
				if (lookahead > maxLookahead) maxLookahead = lookahead;
			}
		}
		
		return maxLookahead;
	}
	
	private long getMemoizedHits() {
		long memoizerHits = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			memoizerHits += counter.memoizedHits;
		}
		
		return memoizerHits;
	}
	
	private long getMemoizedMisses() {
		long memoizerMisses = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
			RuleCounter counter = rule.getValue();
			
			memoizerMisses += counter.memoizedMisses;
		}
		
		return memoizerMisses;
	}
	
	private long getTotalNonMemoizedHitCpuTime() {
		long totalNonMemoizedHitCpuTime = 0;
		
		for (Map.Entry<RuleMatcher, RuleCounter> rule: ruleStats.entrySet()) {
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
	
	private void startMatch(RuleMatcher rule, ParsingState parsingState) {
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
	
	private void startRecordingTime(RuleMatcher rule) {
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
	
	private void abortRecordingTime(RuleMatcher rule) {
		timers.peek().abort();
	}

	private long getCpuTime( ) {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime( ) : 0L;
	}
	
	private static long max(Collection<? extends Number> collection) {
		long max = 0;
		
		for (final Number number: collection) {
			if (number.longValue() > max) max = number.longValue();
		}
		
		return max;
	}
	
	private static double average(Collection<? extends Number> collection) {
		return (collection.size() == 0) ? 0 : sum(collection) / (double)collection.size();
	}
	
	private static long sum(Collection<? extends Number> collection) {
		long sum = 0;
		
		for (final Number number: collection) {
			sum += number.longValue();
		}
		
		return sum;
	}
	
	private static long nsToMs(long ns) {
		return ns / 1000000L;
	}
	
	private static double nsToMs(double ns) {
		return ns / 1000000L;
	}
	
}
