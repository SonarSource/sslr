/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingException;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.ParsingEventListener;

public abstract class Matcher {

  public Matcher[] children;

  protected Matcher(Matcher... children) {
    this.children = children;
  }
  
  public final Matcher[] getChildren() {
  	return this.children;
  }
	
  public final boolean isMatching(ParsingState parsingState) {
    return matchToIndex(parsingState) >= 0;
  }
  
  public final int matchToIndex(ParsingState parsingState) {
    int indexBeforeStarting = parsingState.lexerIndex;
    boolean leftRecursionState = parsingState.hasPendingLeftRecursion();
    try {
      match(parsingState);
      return parsingState.lexerIndex;
    } catch (BacktrackingException re) {
      return -1;
    } finally {
      parsingState.lexerIndex = indexBeforeStarting;
      parsingState.setLeftRecursionState(leftRecursionState);
    }
  }
  
	public abstract AstNode match(ParsingState parsingState);
	
	protected final void enterEvent(ParsingState parsingState) {
  	if (parsingState.getParsingEventListenrs() != null) {
	  	if (this instanceof RuleMatcher) {
		  	/* Fire the enterRule event */
		  	for (ParsingEventListener listener: parsingState.getParsingEventListenrs()) {
		    	listener.enterRule((RuleMatcher)this, parsingState);
		    }
	  	} else {
		  	/* Fire the enterMatcher event */
		  	for (ParsingEventListener listener: parsingState.getParsingEventListenrs()) {
		    	listener.enterMatcher(this, parsingState);
		    }
	  	}
  	}
  }
  
  protected void exitWithMatchEvent(ParsingState parsingState, AstNode astNode) {
  	if (parsingState.getParsingEventListenrs() != null) {
    	if (this instanceof RuleMatcher) {
  	  	/* Fire the exitWithMatchRule event */
  	  	for (ParsingEventListener listener: parsingState.getParsingEventListenrs()) {
  	    	listener.exitWithMatchRule((RuleMatcher)this, parsingState, astNode);
  	    }
    	} else {
  	  	/* Fire the exitWithMatchMatcher event */
  	  	for (ParsingEventListener listener: parsingState.getParsingEventListenrs()) {
  	    	listener.exitWithMatchMatcher(this, parsingState, astNode);
  	    }
    	}
		}
  }
  
  protected void exitWithoutMatchEvent(ParsingState parsingState, BacktrackingException re) {
  	if (parsingState.getParsingEventListenrs() != null) {
    	if (this instanceof RuleMatcher) {
  	  	/* Fire the exitWithoutMatchRule event */
  	  	for (ParsingEventListener listener: parsingState.getParsingEventListenrs()) {
  	    	listener.exitWithoutMatchRule((RuleMatcher)this, parsingState, re);
  	    }
    	} else {
  	  	/* Fire the exitWithoutMatchMatcher event */
  	  	for (ParsingEventListener listener: parsingState.getParsingEventListenrs()) {
  	    	listener.exitWithoutMatchMatcher(this, parsingState, re);
  	    }
    	}
		}
  }
	
}
