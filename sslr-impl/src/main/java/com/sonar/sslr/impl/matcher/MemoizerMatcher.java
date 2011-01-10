/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

class MemoizerMatcher extends Matcher {

  private final Matcher memoizedMatcher;

  public MemoizerMatcher(Matcher proxiedMatcher) {
    this.memoizedMatcher = proxiedMatcher;
  }

  public boolean isMatching(ParsingState state) {
    if (state.hasMemoizedAst(this)) {
      return true;
    }
    int startingIndex = state.lexerIndex;
    try {
      AstNode node = memoizedMatcher.match(state);
      memoizeAstNode(node, startingIndex, state);
      return true;
    } catch (RecognitionExceptionImpl e) {
      return false;
    } finally {
      state.lexerIndex = startingIndex;
    }
  }

  private void memoizeAstNode(AstNode node, int startingIndex, ParsingState state) {
    if (node != null) {
      node.setFromIndex(startingIndex);
      node.setToIndex(state.lexerIndex);
      state.memoizeAst(this, node);
    }
  }

  @Override
  public AstNode match(ParsingState state) {
    if (state.hasMemoizedAst(this)) {
      return state.getMemoizedAst(this);
    }
    int startingIndex = state.lexerIndex;
    AstNode node = memoizedMatcher.match(state);
    memoizeAstNode(node, startingIndex, state);
    return node;
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    memoizedMatcher.setParentRule(parentRule);
  }

  public String toString() {
    return memoizedMatcher.toString();
  }

}
