/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import java.util.List;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
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

  public void setChild(int i, Matcher child) {
  	this.children[i] = child;
  }
  
  public String getDefinition() {
  	return getDefinition(true);
  }
  
  public abstract String getDefinition(boolean isRoot);

  @Override
  public String toString() {
  	return getDefinition();
  }
  
  public void setParentRule(RuleImpl parentRule) {
  	this.parentRule = parentRule;
  	for (Matcher child: this.children) {
			child.setParentRule(parentRule);
		}
  }

  public RuleImpl getRule() {
    return parentRule;
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

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return true;
  }

  public abstract AstNode match(ParsingState parsingState);

  public final AstNode parse(ParsingState parsingState) {
    try {
      return this.match(parsingState);
    } catch (RecognitionExceptionImpl e) {
      throw new RecognitionExceptionImpl(parsingState);
    }
  }

  public final AstNode parse(List<Token> tokens) {
    return parse(new ParsingState(tokens));
  }
  
}
