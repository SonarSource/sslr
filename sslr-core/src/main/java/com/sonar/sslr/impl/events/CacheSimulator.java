/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoizedMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class CacheSimulator extends ParsingEventListener {
	
	private int maximalCacheSize = 50;
	
	private final Stack<Integer> cacheSizeUpperBoundStackPositive = new Stack<Integer>();
	private final Stack<Integer> cacheSizeUpperBoundStackNegative = new Stack<Integer>();
	private final FastStackMatcherAndPosition currentStack = new FastStackMatcherAndPosition();
	private final ListMultimap<Matcher, Integer> matchesCache = LinkedListMultimap.create();
	private final ListMultimap<Matcher, Integer> mismatchesCache = LinkedListMultimap.create();
	private final HashMap<Matcher, HashSet<Integer>> matchesSet = new HashMap<Matcher, HashSet<Integer>>();
	private final HashMap<Matcher, HashSet<Integer>> mismatchesSet = new HashMap<Matcher, HashSet<Integer>>();
	
	private HashMap<Integer, Integer> matchesHits;
	private HashMap<Integer, Integer> matchesMisses;
	private HashMap<Integer, Integer> mismatchesHits;
	private HashMap<Integer, Integer> mismatchesMisses;
	private int currentMemoizerHits;
	private int currentMemoizerMisses;
	
	public CacheSimulator() {
		initialize();
	}
	
	public void initialize() {
		matchesHits = new HashMap<Integer, Integer>();
		matchesMisses = new HashMap<Integer, Integer>();
		mismatchesHits = new HashMap<Integer, Integer>();
		mismatchesMisses = new HashMap<Integer, Integer>();
		
		initializeHashMap(matchesHits);
		initializeHashMap(matchesMisses);
		initializeHashMap(mismatchesHits);
		initializeHashMap(mismatchesMisses);
		
		currentMemoizerHits = 0;
		currentMemoizerMisses = 0;
	}
	
	private void initializeHashMap(HashMap<Integer, Integer> hashMap) {
		for (int i = 1; i <= maximalCacheSize; i++) {
			hashMap.put(i, 0);
		}
		hashMap.put(maximalCacheSize + 1, 0);
	}
	
	private void incrementHashMap(HashMap<Integer, Integer> hashMap, int cacheSize) {
		hashMap.put(cacheSize, hashMap.get(cacheSize) + 1);
	}
	
	private void addToCacheSet(HashMap<Matcher, HashSet<Integer>> cacheSet, Matcher matcher, int fromIndex) {
		HashSet<Integer> set = cacheSet.get(matcher);
		if (set == null) {
			set = new HashSet<Integer>();
			cacheSet.put(matcher, set);
		}
		set.add(fromIndex);
	}
	
	private int getFirstHit(HashMap<Matcher, HashSet<Integer>> cacheSet, int cacheSizeUpperBound, Matcher matcher, List<Integer> cache, int lexerIndex) {
		if (cacheSizeUpperBound <= 0) return -1;
		if (!cacheSet.containsKey(matcher) || !cacheSet.get(matcher).contains(lexerIndex)) return -1;
		
		ListIterator<Integer> reverseIterator = cache.listIterator(cache.size());
		
		Set<Integer> distinctCacheEntries = new HashSet<Integer>();
		int cacheSize = 0;
		while (reverseIterator.hasPrevious()) {
			Integer cachedIndex = reverseIterator.previous();

			if (!distinctCacheEntries.add(cachedIndex)) continue;
			cacheSize++;
			
			if (cachedIndex == lexerIndex) {
				/* Hit (else miss) */
				return cacheSize;
			} else if (cacheSize == maximalCacheSize) {
				/* The first hit was from the full mode */
				return maximalCacheSize + 1;
			} else if (cacheSize == cacheSizeUpperBound) {
				/* Not in cache */
				return -1;
			}
		}
		
		/* Cache is larger than the number of attempts so far, and no match was found */
		return -1;
	}
	
	private int lookupInCaches(HashMap<Matcher, HashSet<Integer>> cacheSet, int cacheSizeUpperBound, Matcher matcher, List<Integer> cache, int lexerIndex, HashMap<Integer, Integer> hits, HashMap<Integer, Integer> misses) {
		/* Compute the first hit */
		int firstHit = getFirstHit(cacheSet, cacheSizeUpperBound, matcher, cache, lexerIndex);
		
		/* Report the misses */
		if (firstHit == -1) {
			for (int cacheSize = 1; cacheSize <= cacheSizeUpperBound; cacheSize++) {
				incrementHashMap(misses, cacheSize);
			}
		} else {
			for (int cacheSize = 1; cacheSize < firstHit; cacheSize++) {
				incrementHashMap(misses, cacheSize);
			}
		}
		
		/* Report the hits */
		if (firstHit != -1) {
			for (int cacheSize = firstHit; cacheSize <= cacheSizeUpperBound; cacheSize++) {
				incrementHashMap(hits, cacheSize);
			}
		}
		
		/* Return the new cache upper bound */
		return (firstHit == -1) ? cacheSizeUpperBound : firstHit - 1;
	}
	
	@Override
	public void beginParse() {
		currentStack.clear();
		matchesCache.clear();
		mismatchesCache.clear();
		matchesSet.clear();
		mismatchesSet.clear();
		
		/* Set the initial cache size upper bound to full mode */
		cacheSizeUpperBoundStackPositive.clear();
		cacheSizeUpperBoundStackPositive.push(maximalCacheSize + 1);
		
		cacheSizeUpperBoundStackNegative.clear();
		cacheSizeUpperBoundStackNegative.push(maximalCacheSize + 1);
	}
	
	@Override
	public void endParse() {
		System.out.println("Statistics per cache size:");
		for (int i = 1; i <= maximalCacheSize + 1; i++) {
			System.out.println("\t" + ((i == maximalCacheSize + 1) ? "Full" : String.format("%4d", i)) + ": " + String.format("%10d", matchesHits.get(i)) + " matches hits, " + String.format("%10d", matchesMisses.get(i)) + " matches misses, " + String.format("%10d", matchesHits.get(i) + matchesMisses.get(i)) + " total, " + String.format("%10d", mismatchesHits.get(i)) + " mismatches hits, " + String.format("%10d", mismatchesMisses.get(i)) + " mismatches misses, " + String.format("%10d", mismatchesHits.get(i) + mismatchesMisses.get(i)) + " total");
		}
		System.out.println("Current memoizer: " + currentMemoizerHits + " hits, " + currentMemoizerMisses + " misses, total visited matchers = " + (currentMemoizerHits + currentMemoizerMisses));
	}

	@Override
	public void enterRule(RuleMatcher rule, ParsingState parsingState) {
		cacheSizeUpperBoundStackPositive.push(lookupInCaches(matchesSet, cacheSizeUpperBoundStackPositive.peek(), rule, matchesCache.get(rule), parsingState.lexerIndex, matchesHits, matchesMisses));
		cacheSizeUpperBoundStackNegative.push(lookupInCaches(mismatchesSet, cacheSizeUpperBoundStackNegative.peek(), rule, mismatchesCache.get(rule), parsingState.lexerIndex, mismatchesHits, mismatchesMisses));
		
		currentStack.push(rule, parsingState.lexerIndex);
	}

	@Override
	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
		cacheSizeUpperBoundStackNegative.pop();
		cacheSizeUpperBoundStackPositive.pop();
		if (astNode != null) {
			int fromIndex = currentStack.peekFromIndex();
			addToCacheSet(matchesSet, rule, fromIndex);
			matchesCache.put(rule, fromIndex);
		}
		currentStack.pop();
	}

	@Override
	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
		cacheSizeUpperBoundStackNegative.pop();
		cacheSizeUpperBoundStackPositive.pop();
		int fromIndex = currentStack.peekFromIndex();
		addToCacheSet(mismatchesSet, rule, fromIndex);
		mismatchesCache.put(rule, fromIndex);
		currentStack.pop();
	}

	@Override
	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		cacheSizeUpperBoundStackPositive.push(lookupInCaches(matchesSet, cacheSizeUpperBoundStackPositive.peek(), matcher, matchesCache.get(matcher), parsingState.lexerIndex, matchesHits, matchesMisses));
		cacheSizeUpperBoundStackNegative.push(lookupInCaches(mismatchesSet, cacheSizeUpperBoundStackNegative.peek(), matcher, matchesCache.get(matcher), parsingState.lexerIndex, mismatchesHits, mismatchesMisses));
		
		currentStack.push(matcher, parsingState.lexerIndex);
	}

	@Override
	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
		cacheSizeUpperBoundStackNegative.pop();
		cacheSizeUpperBoundStackPositive.pop();
		if (astNode != null) {
			int fromIndex = currentStack.peekFromIndex();
			addToCacheSet(matchesSet, matcher, fromIndex);
			matchesCache.put(matcher, fromIndex);
		}
		currentStack.pop();
	}

	@Override
	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
		cacheSizeUpperBoundStackNegative.pop();
		cacheSizeUpperBoundStackPositive.pop();
		int fromIndex = currentStack.peekFromIndex();
		addToCacheSet(mismatchesSet, matcher, fromIndex);
		mismatchesCache.put(matcher, fromIndex);
		currentStack.pop();
	}

	@Override
	public void memoizerHit(MemoizedMatcher matcher, ParsingState parsingState) {
		currentMemoizerHits++;
	}

	@Override
	public void memoizerMiss(MemoizedMatcher matcher, ParsingState parsingState) {
		currentMemoizerMisses++;
	}
	
}
