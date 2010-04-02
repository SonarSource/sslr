/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.impl.ParsingState;
import com.sonarsource.sslr.impl.RecognitionExceptionImpl;

class ProxyMatcher extends Matcher {

  private final Matcher proxiedMatcher;

  public ProxyMatcher(Matcher proxiedMatcher) {
    this.proxiedMatcher = proxiedMatcher;
  }

  public boolean isMatching(ParsingState state) {
    if (state.hasMemoizedAst(this)) {
      return true;
    }
    int startingIndex = state.lexerIndex;
    try {
      AstNode node = proxiedMatcher.match(state);
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
  protected AstNode match(ParsingState state) {
    if (state.hasMemoizedAst(this)) {
      return state.getMemoizedAst(this);
    }
    int startingIndex = state.lexerIndex;
    AstNode node = proxiedMatcher.match(state);
    memoizeAstNode(node, startingIndex, state);
    return node;
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    proxiedMatcher.setParentRule(parentRule);
  }

  public String toString() {
    return proxiedMatcher.toString();
  }

}
