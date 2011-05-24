/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.events;

import org.apache.commons.lang.NotImplementedException;

import com.sonar.sslr.api.AstListener;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class RuleImplAdapter extends RuleImpl {

  private RuleImpl ruleImpl;
  private ParsingEventListener parsingEventListener;

  public RuleImplAdapter(ParsingEventListener parsingEventListener, RuleImpl ruleImpl) {
    super(ruleImpl.getName());

    this.ruleImpl = ruleImpl;
    this.parsingEventListener = parsingEventListener;
    this.children = new Matcher[]{ruleImpl};
  }
  
  public RuleImpl getRuleImpl() {
  	return ruleImpl;
  }

  @Override
  public AstNode match(ParsingState parsingState) {
    parsingEventListener.enterRule(ruleImpl, parsingState);

    try {
      AstNode astNode = this.ruleImpl.match(parsingState);
      parsingEventListener.exitWithMatchRule(ruleImpl, parsingState, astNode);
      return astNode;
    } catch (RecognitionExceptionImpl re) {
      parsingEventListener.exitWithoutMatchRule(ruleImpl, parsingState, re);
      throw re;
    }
    
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    return this.ruleImpl.hasToBeSkippedFromAst(node);
  }

  @Override
  public RuleImpl is(Object... matchers) {
    throw new NotImplementedException();
  }

  @Override
  public RuleImpl override(Object... matchers) {
  	throw new NotImplementedException();
  }

  @Override
  public void mockUpperCase() {
    this.ruleImpl.mockUpperCase();
  }

  @Override
  public void mock() {
    this.ruleImpl.mock();
  }

  @Override
  public RuleImpl isOr(Object... matchers) {
  	throw new NotImplementedException();
  }

  @Override
  public RuleImpl or(Object... matchers) {
  	throw new NotImplementedException();
  }

  @Override
  public RuleImpl and(Object... matchers) {
  	throw new NotImplementedException();
  }

  @Override
  public RuleImpl orBefore(Object... matchers) {
  	throw new NotImplementedException();
  }

  @Override
  public RuleImpl skip() {
    this.ruleImpl.skip();
    return this;
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.ruleImpl.setParentRule(parentRule);
  }

  @Override
  public RuleImpl getParentRule() {
    return this.ruleImpl.getParentRule();
  }

  @Override
  public RuleImpl getRule() {
    return this;
  }

  @Override
  public String toString() {
    return "RuleImplAdapter";
  }

  @Override
  public RuleImpl setListener(AstListener listener) {
    this.ruleImpl.setListener(listener);
    return this;
  }

  @Override
  public RuleImpl skipIf(AstNodeType astNodeSkipPolicy) {
    this.ruleImpl.skipIf(astNodeSkipPolicy);
    return this;
  }

  @Override
  public RuleImpl skipIfOneChild() {
    this.ruleImpl.skipIfOneChild();
    return this;
  }

  @Override
  public RuleImpl plug(Class adapterClass) {
    this.ruleImpl.plug(adapterClass);
    return this;
  }

  @Override
  public Class getAdapter() {
    return this.ruleImpl.getAdapter();
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
