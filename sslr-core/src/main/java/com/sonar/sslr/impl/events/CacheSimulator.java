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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public final class CacheSimulator extends ParsingEventListener {
	
	private int maximalCacheSize = 50;
	
	private FastStackMatcherAndPosition currentStack = new FastStackMatcherAndPosition();
	private ListMultimap<Matcher, Integer> matchesCache = LinkedListMultimap.create();
	private ListMultimap<Matcher, Integer> mismatchesCache = LinkedListMultimap.create();
	private HashMap<Matcher, HashSet<Integer>> matchesSet = new HashMap<Matcher, HashSet<Integer>>();
	private HashMap<Matcher, HashSet<Integer>> mismatchesSet = new HashMap<Matcher, HashSet<Integer>>();
	
	private HashMap<Integer, Integer> matchesHits;
	private HashMap<Integer, Integer> matchesMisses;
	private HashMap<Integer, Integer> mismatchesHits;
	private HashMap<Integer, Integer> mismatchesMisses;
	
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
	
	private void lookupInCache(HashMap<Matcher, HashSet<Integer>> cacheSet, Matcher matcher, List<Integer> cache, HashMap<Integer, Integer> hit, HashMap<Integer, Integer> miss, int lexerIndex) {
		ListIterator<Integer> reverseIterator = cache.listIterator(cache.size());
		
		if (cacheSet.containsKey(matcher) && cacheSet.get(matcher).contains(lexerIndex)) {
			Set<Integer> alreadyCounted = new HashSet<Integer>();
			int cacheSize = 0;
			while (reverseIterator.hasPrevious()) {
				Integer cachedIndex = reverseIterator.previous();

				if (alreadyCounted.contains(cachedIndex)) continue;
				alreadyCounted.add(cachedIndex);
				cacheSize++;
				
				if (cachedIndex == lexerIndex) {
					incrementHashMap(hit, cacheSize);
					return;
				} else if (cacheSize == maximalCacheSize) {
					/* The hit came from the full mode */
					incrementHashMap(hit, maximalCacheSize + 1);
					return;
				}
			}
			
			throw new IllegalStateException("We should never get here, cacheSize = " + cacheSize + ", maximalCacheSize = " + maximalCacheSize);
		}
		
		/* Not found in cache */
		incrementHashMap(miss, maximalCacheSize + 1);
	}
	
	private void lookup(Matcher matcher, int lexerIndex) {
		lookupInCache(matchesSet, matcher, matchesCache.get(matcher), matchesHits, matchesMisses, lexerIndex);
		lookupInCache(mismatchesSet, matcher, mismatchesCache.get(matcher), mismatchesHits, mismatchesMisses, lexerIndex);
	}
	
	@Override
	public void beginParse() {
		currentStack.clear();
		matchesCache.clear();
		mismatchesCache.clear();
		matchesSet.clear();
		mismatchesSet.clear();
	}
	
	@Override
	public void endParse() {
		System.out.println("Hits:");
		for (int i = 1; i <= maximalCacheSize; i++) {
			System.out.println("\t" + String.format("%4d", i) + ": " + String.format("%8d", matchesHits.get(i)) + " matches hits, " + String.format("%8d", mismatchesHits.get(i)) + " mismatches hits");
		}
		System.out.println("\tFull: " + String.format("%8d", matchesHits.get(maximalCacheSize + 1)) + " matches hits, " + String.format("%8d", mismatchesHits.get(maximalCacheSize + 1)) + " mismatches hits");
		System.out.println("Misses:");
		System.out.println("\tFull: " + String.format("%8d", matchesMisses.get(maximalCacheSize + 1)) + " matches misses, " + String.format("%8d", mismatchesMisses.get(maximalCacheSize + 1)) + " mismatches misses");
	}

	@Override
	public void enterRule(RuleMatcher rule, ParsingState parsingState) {
		lookup(rule, parsingState.lexerIndex);
		currentStack.push(rule, parsingState.lexerIndex);
	}

	@Override
	public void exitWithMatchRule(RuleMatcher rule, ParsingState parsingState, AstNode astNode) {
		int fromIndex = currentStack.peekFromIndex();
		addToCacheSet(matchesSet, rule, fromIndex);
		matchesCache.put(rule, fromIndex);
		currentStack.pop();
	}

	@Override
	public void exitWithoutMatchRule(RuleMatcher rule, ParsingState parsingState) {
		int fromIndex = currentStack.peekFromIndex();
		addToCacheSet(mismatchesSet, rule, fromIndex);
		mismatchesCache.put(rule, fromIndex);
		currentStack.pop();
	}

	@Override
	public void enterMatcher(Matcher matcher, ParsingState parsingState) {
		lookup(matcher, parsingState.lexerIndex);
		currentStack.push(matcher, parsingState.lexerIndex);
	}

	@Override
	public void exitWithMatchMatcher(Matcher matcher, ParsingState parsingState, AstNode astNode) {
		int fromIndex = currentStack.peekFromIndex();
		addToCacheSet(matchesSet, matcher, fromIndex);
		matchesCache.put(matcher, fromIndex);
		currentStack.pop();
	}

	@Override
	public void exitWithoutMatchMatcher(Matcher matcher, ParsingState parsingState) {
		int fromIndex = currentStack.peekFromIndex();
		addToCacheSet(mismatchesSet, matcher, fromIndex);
		mismatchesCache.put(matcher, fromIndex);
		currentStack.pop();
	}
	
}
