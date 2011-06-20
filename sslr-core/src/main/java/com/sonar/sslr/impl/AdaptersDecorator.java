/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.util.HashSet;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.GrammarDecorator;
import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.ParsingEventListener;
import com.sonar.sslr.impl.events.RuleMatcherAdapter;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MatcherTreePrinter;
import com.sonar.sslr.impl.matcher.MemoizerMatcher;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;
import com.sonar.sslr.impl.matcher.TokenTypeClassMatcher;
import com.sonar.sslr.impl.matcher.TokenTypeMatcher;
import com.sonar.sslr.impl.matcher.TokenValueMatcher;

public class AdaptersDecorator<GRAMMAR extends Grammar> implements GrammarDecorator<GRAMMAR> {

  private HashSet<Matcher> visited;
  private final ParsingEventListener[] parsingEventListeners;
  private final boolean enableMemoization;
  
  public AdaptersDecorator(boolean enableMemoization, ParsingEventListener... parsingEventListeners) {
    this.parsingEventListeners = parsingEventListeners;
    this.enableMemoization = enableMemoization;
  }
  
  private Matcher memoize(Matcher matcher) {
  	if (enableMemoization) {
			if (!(matcher instanceof TokenValueMatcher) && !(matcher instanceof TokenTypeMatcher) && !(matcher instanceof TokenTypeClassMatcher)) {
				return new MemoizerMatcher(matcher);
			}
  	}
		
		return matcher;
  }
  
  private Matcher eventize(Matcher originalMatcher, Matcher memoizedMatcher) {
  	if (parsingEventListeners.length > 0) {
  		if (originalMatcher instanceof RuleMatcher) {
  			return new RuleMatcherAdapter(memoizedMatcher, parsingEventListeners);
  		} else {
  			return new MatcherAdapter(memoizedMatcher, parsingEventListeners);
  		}
  	}
  	
  	return memoizedMatcher;
  }

  private void decorateMatcher(Matcher matcher) {
    /* Visitor logic */
    if (visited.contains(matcher)) {
    	/* This matcher was already visited */
      return;
    }
    visited.add(matcher);

    for (int i = 0; i < matcher.getChildren().length; i++) {
      decorateMatcher(matcher.getChildren()[i]); /* Recursive */
      
      Matcher originalChild = matcher.getChildren()[i];
      visited.add(originalChild);
      
      Matcher memoizedChild = memoize(originalChild);
      visited.add(memoizedChild);
      
      Matcher eventizedChild = eventize(originalChild, memoizedChild);
      visited.add(eventizedChild);
      
      matcher.getChildren()[i] = eventizedChild;
    }
  }

  public void decorate(GRAMMAR grammar) {
    RuleDefinition root = (RuleDefinition) grammar.getRootRule();

    visited = new HashSet<Matcher>();
    
    RuleMatcher originalRule = root.getRule();
    Matcher memoizedRule = memoize(originalRule);
    if (originalRule != memoizedRule) visited.add(memoizedRule);
    Matcher eventizedRule = eventize(originalRule, memoizedRule);
    if (originalRule != eventizedRule) visited.add(eventizedRule);
    
    root.setRuleMatcher(!(eventizedRule instanceof RuleMatcher) ? originalRule : (RuleMatcher)eventizedRule);

    decorateMatcher((memoizedRule instanceof MemoizerMatcher) ? memoizedRule.getChildren()[0] : memoizedRule); /* Change the whole tree, recursively! */
  }

  public ParsingEventListener[] getParsingEventListeners() {
    return this.parsingEventListeners;
  }

}
