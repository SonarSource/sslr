/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class MemoizerMatcher extends Matcher {

  public MemoizerMatcher(Matcher proxiedMatcher) {
    super(proxiedMatcher);
  }

  public int matchToIndex(ParsingState state) {
    if (state.hasMemoizedAst(this)) {
      return state.getMemoizedAst(this).getToIndex();
    }
    int startingIndex = state.lexerIndex;
    try {
      AstNode node = super.children[0].match(state);
      memoizeAstNode(node, startingIndex, state);
      return state.lexerIndex;
    } catch (RecognitionExceptionImpl e) {
      return -1;
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
    AstNode node = super.children[0].match(state);
    memoizeAstNode(node, startingIndex, state);
    return node;
  }
  
  @Override
  public String getDefinition(boolean isRoot) {
  	return "MemoizerMatcher(" + super.children[0].getDefinition(false) + ")";
  }

}
