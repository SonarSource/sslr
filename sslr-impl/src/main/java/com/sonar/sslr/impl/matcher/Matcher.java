/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public abstract class Matcher implements AstNodeSkippingPolicy {

  protected RuleImpl parentRule;
  protected Matcher[] children;

  public Matcher(Matcher... children) {
    this.children = children;
  }

  public Matcher[] getChildren() {
    return this.children;
  }

  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    for (Matcher child : this.children) {
      child.setParentRule(parentRule);
    }
  }

  public RuleImpl getRule() {
    return parentRule;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return true;
  }

  public final boolean isMatching(ParsingState parsingState) {
    return matchToIndex(parsingState) >= 0;
  }

  public int matchToIndex(ParsingState parsingState) {
    int indexBeforeStarting = parsingState.lexerIndex;
    boolean leftRecursionState = parsingState.hasPendingLeftRecursion();
    try {
      match(parsingState);
      return parsingState.lexerIndex;
    } catch (RecognitionExceptionImpl e) {
      return -1;
    } finally {
      parsingState.lexerIndex = indexBeforeStarting;
      parsingState.setLeftRecursionState(leftRecursionState);
    }
  }

  public abstract AstNode match(ParsingState parsingState);

}
