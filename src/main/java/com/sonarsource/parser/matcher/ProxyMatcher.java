/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.RecognitionException;
import com.sonarsource.parser.ast.AstNode;

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
    } catch (RecognitionException e) {
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
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
    proxiedMatcher.setParentRule(parentRule);
  }

  public String toString() {
    return proxiedMatcher.toString();
  }

}
