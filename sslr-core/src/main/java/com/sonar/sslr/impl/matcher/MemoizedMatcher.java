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

public abstract class MemoizedMatcher extends Matcher {
	
	public MemoizedMatcher(Matcher... children) {
		super(children);
	}

  public final AstNode match(ParsingState parsingState) {
  	enterEvent(parsingState);
  	
  	/* Memoizer lookup */
  	AstNode memoizedAstNode = parsingState.getMemoizedAst(this);
  	if (memoizedAstNode != null) {
  		memoizerHitEvent(parsingState);
  		parsingState.lexerIndex = memoizedAstNode.getToIndex();
  		exitWithMatchEvent(parsingState, memoizedAstNode);
      return memoizedAstNode;
  	}
  	memoizerMissEvent(parsingState);
  	
  	try {
  		int startingIndex = parsingState.lexerIndex;
  		AstNode astNode = matchWorker(parsingState);
  		if (astNode != null) {
        astNode.setFromIndex(startingIndex);
        astNode.setToIndex(parsingState.lexerIndex);
        parsingState.memoizeAst(this, astNode);
  		}
  		
  		exitWithMatchEvent(parsingState, astNode);
    	return astNode;
  	} catch (BacktrackingException re) {
  		exitWithoutMatchEvent(parsingState, re);
  		throw re;
  	}
  }

  protected abstract AstNode matchWorker(ParsingState parsingState);
  
  private void memoizerHitEvent(ParsingState parsingState) {
  	if (parsingState.getParsingEventListenrs() != null) {
  		for (ParsingEventListener parsingEventListener: parsingState.getParsingEventListenrs()) {
    		parsingEventListener.memoizerHit(this, parsingState);
    	}
  	}
  }
  
  private void memoizerMissEvent(ParsingState parsingState) {
  	if (parsingState.getParsingEventListenrs() != null) {
  		for (ParsingEventListener parsingEventListener: parsingState.getParsingEventListenrs()) {
    		parsingEventListener.memoizerMiss(this, parsingState);
    	}
  	}
  }
  
}
