/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.events.ParsingEventListener;

public class MemoizerMatcher extends Matcher {
	
	private final ParsingEventListener[] parsingEventListeners;

  public MemoizerMatcher(Matcher proxiedMatcher, ParsingEventListener... parsingEventListeners) {
    super(proxiedMatcher);
    this.parsingEventListeners = parsingEventListeners;
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
  	AstNode memoizedAstNode = state.getMemoizedAst(this);
    if (memoizedAstNode != null) {

    	for (ParsingEventListener parsingEventListener: parsingEventListeners) {
    		parsingEventListener.memoizerHit(super.children[0], state);
    	}
    	
      state.lexerIndex = memoizedAstNode.getToIndex();
      return memoizedAstNode;
    }
    
    for (ParsingEventListener parsingEventListener: parsingEventListeners) {
  		parsingEventListener.memoizerMiss(super.children[0], state);
  	}
    
    int startingIndex = state.lexerIndex;
    AstNode node = super.children[0].match(state);
    memoizeAstNode(node, startingIndex, state);
    return node;
  }
  
  @Override
  public String toString() {
  	return "MemoizerMatcher";
  }
  
}
