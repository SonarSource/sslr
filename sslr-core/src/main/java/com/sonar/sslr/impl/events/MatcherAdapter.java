/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoizerMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class MatcherAdapter extends Matcher {

  private final Matcher matcher;
  private final Matcher matcherMemoized;
  private final ParsingEventListener[] parsingEventListeners;

  public MatcherAdapter(Matcher matcherMemoized, ParsingEventListener... parsingEventListeners) {
    this.matcher = (matcherMemoized instanceof MemoizerMatcher) ? matcherMemoized.getChildren()[0] : matcherMemoized;
    this.matcherMemoized = matcherMemoized;
    this.parsingEventListeners = parsingEventListeners;
    this.children = new Matcher[] { matcherMemoized };
  }

  @Override
  public AstNode match(ParsingState parsingState) {
  	for (ParsingEventListener parsingEventListener: parsingEventListeners) {
  		parsingEventListener.enterMatcher(matcher, parsingState);
  	}

    try {
      AstNode astNode = this.matcherMemoized.match(parsingState);
      for (ParsingEventListener parsingEventListener: parsingEventListeners) {
      	parsingEventListener.exitWithMatchMatcher(matcher, parsingState, astNode);
      }
      return astNode;
    } catch (BacktrackingException re) {
    	for (ParsingEventListener parsingEventListener: parsingEventListeners) {
    		parsingEventListener.exitWithoutMatchMatcher(matcher, parsingState, re);
    	}
      throw re;
    }

  }

  @Override
  public boolean equals(Object obj) {
    return this.matcher.equals(obj);
  }

  @Override
  public int hashCode() {
    return this.matcher.hashCode();
  }

  @Override
  public String toString() {
    return "MatcherAdapter";
  }

}
