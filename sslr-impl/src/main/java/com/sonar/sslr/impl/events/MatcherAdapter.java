/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class MatcherAdapter extends Matcher {

  private Matcher matcher;
  private ParsingEventListener parsingEventListener;

  public MatcherAdapter(ParsingEventListener parsingEventListener, Matcher matcher) {
    this.matcher = matcher;
    this.parsingEventListener = parsingEventListener;
  }

  @Override
  public Matcher[] getChildren() {
    return this.matcher.getChildren();
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.matcher.setParentRule(parentRule);
  }

  @Override
  public RuleImpl getRule() {
    return this.matcher.getRule();
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    return this.matcher.hasToBeSkippedFromAst(node);
  }

  @Override
  public AstNode match(ParsingState parsingState) {
    parsingEventListener.enterMatcher(matcher, parsingState);

    try {
      AstNode astNode = this.matcher.match(parsingState);
      parsingEventListener.exitWithMatchMatcher(matcher, parsingState, astNode);
      return astNode;
    } catch (RecognitionExceptionImpl re) {
      parsingEventListener.exitWithoutMatchMatcher(matcher, parsingState, re);
      throw re;
    }
  }

  @Override
  public String getDefinition(boolean isRoot) {
    return "MatcherAdapter(" + this.matcher.getDefinition(isRoot) + ")";
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
    return this.matcher.toString();
  }

}
