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

public class MatcherAdapter extends Matcher {

  private Matcher matcher;
  private ParsingEventListener parsingEventListener;

  public MatcherAdapter(ParsingEventListener parsingEventListener, Matcher matcher) {
    this.matcher = matcher;
    this.parsingEventListener = parsingEventListener;
    this.children = new Matcher[] { matcher };
  }

  @Override
  public AstNode match(ParsingState parsingState) {
    parsingEventListener.enterMatcher(matcher, parsingState);

    try {
      AstNode astNode = this.matcher.match(parsingState);
      parsingEventListener.exitWithMatchMatcher(matcher, parsingState, astNode);
      return astNode;
    } catch (BacktrackingException re) {
      parsingEventListener.exitWithoutMatchMatcher(matcher, parsingState, re);
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
