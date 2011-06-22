/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.MemoizerMatcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class RuleMatcherAdapter extends RuleMatcher {

  private final RuleMatcher ruleImpl;
  private final Matcher ruleImplMemoized;
  private final ParsingEventListener[] parsingEventListeners;

  public RuleMatcherAdapter(Matcher ruleImplMemoized, ParsingEventListener... parsingEventListeners) {
    super((ruleImplMemoized instanceof MemoizerMatcher) ? ((RuleMatcher)ruleImplMemoized.getChildren()[0]).getName() : ((RuleMatcher)ruleImplMemoized).getName());

    this.ruleImpl = (ruleImplMemoized instanceof MemoizerMatcher) ? (RuleMatcher)ruleImplMemoized.getChildren()[0] : (RuleMatcher)ruleImplMemoized;
    this.ruleImplMemoized = ruleImplMemoized;
    this.parsingEventListeners = parsingEventListeners;
    this.children = new Matcher[] { ruleImplMemoized };
  }

  public RuleMatcher getRuleImpl() {
    return ruleImpl;
  }

  @Override
  public AstNode match(ParsingState parsingState) {
  	for (ParsingEventListener parsingEventListener: parsingEventListeners) {
  		parsingEventListener.enterRule(ruleImpl, parsingState);
  	}

    try {
      AstNode astNode = this.ruleImplMemoized.match(parsingState);
      for (ParsingEventListener parsingEventListener: parsingEventListeners) {
      	parsingEventListener.exitWithMatchRule(ruleImpl, parsingState, astNode);
      }
      return astNode;
    } catch (BacktrackingException re) {
    	for (ParsingEventListener parsingEventListener: parsingEventListeners) {
    		parsingEventListener.exitWithoutMatchRule(ruleImpl, parsingState, re);
    	}
      throw re;
    }

  }
  
  @Override
  public void setNodeType(AstNodeType astNodeType) {
  	super.setNodeType(astNodeType);
    this.ruleImpl.setNodeType(astNodeType);
  }

  @Override
  public String toString() {
    return "RuleImplAdapter";
  }

  @Override
  public void setListener(AstListener listener) {
    this.ruleImpl.setListener(listener);
  }

  @Override
  public String getName() {
    return this.ruleImpl.getName();
  }

  @Override
  public void recoveryRule() {
    this.ruleImpl.recoveryRule();
  }

  @Override
  public boolean equals(Object obj) {
    return this.ruleImpl.equals(obj);
  }

  @Override
  public int hashCode() {
    return this.ruleImpl.hashCode();
  }

  @Override
  public void endParsing() {
    this.ruleImpl.endParsing();
  }

}
