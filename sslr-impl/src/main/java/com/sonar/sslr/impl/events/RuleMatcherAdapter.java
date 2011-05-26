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
import com.sonar.sslr.impl.RecognitionExceptionImpl;
import com.sonar.sslr.impl.matcher.Matcher;
import com.sonar.sslr.impl.matcher.RuleMatcher;

public class RuleMatcherAdapter extends RuleMatcher {

  private RuleMatcher ruleImpl;
  private ParsingEventListener parsingEventListener;

  public RuleMatcherAdapter(ParsingEventListener parsingEventListener, RuleMatcher ruleImpl) {
    super(ruleImpl.getName());

    this.ruleImpl = ruleImpl;
    this.parsingEventListener = parsingEventListener;
    this.children = new Matcher[] { ruleImpl };
  }

  public RuleMatcher getRuleImpl() {
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
  public String toString() {
    return "RuleImplAdapter";
  }

  @Override
  public RuleMatcher setListener(AstListener listener) {
    this.ruleImpl.setListener(listener);
    return this;
  }

  @Override
  public RuleMatcher skipIf(AstNodeType astNodeSkipPolicy) {
    this.ruleImpl.skipIf(astNodeSkipPolicy);
    return this;
  }

  @Override
  public RuleMatcher plug(Class adapterClass) {
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
