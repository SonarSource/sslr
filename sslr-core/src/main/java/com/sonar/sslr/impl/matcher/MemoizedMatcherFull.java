package com.sonar.sslr.impl.matcher;
/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

// Full memoizer strategy (2)

//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import com.sonar.sslr.api.AstNode;
//import com.sonar.sslr.impl.BacktrackingEvent;
//import com.sonar.sslr.impl.ParsingState;
//import com.sonar.sslr.impl.events.ParsingEventListener;
//
//public abstract class MemoizedMatcherFull extends Matcher {
//	
//	private static final boolean enableNegativeMemoization = false;
//
//	private final Map<Integer, AstNode> memoizedAstNodes = new HashMap<Integer, AstNode>();
//	private final Set<Integer> memoizedErrors = new HashSet<Integer>();
//	
//  @Override
//  public void reinitialize() {
//  	memoizedAstNodes.clear();
//  	memoizedErrors.clear();
//  }
//	
//  public MemoizedMatcherFull(Matcher... children) {
//    super(children);
//  }
//
//  public final AstNode match(ParsingState parsingState) {
//    enterEvent(parsingState);
//
//    /* Memoizer lookup */
//    try {
//	    AstNode memoizedAstNode = getMemoizedAst(parsingState);
//	    if (memoizedAstNode != null) {
//	      memoizerHitEvent(parsingState);
//	      parsingState.lexerIndex = memoizedAstNode.getToIndex();
//	      exitWithMatchEvent(parsingState, memoizedAstNode);
//	      return memoizedAstNode;
//	    }
//    } catch (BacktrackingEvent re) {
//    	memoizerHitEvent(parsingState);
//    	exitWithoutMatchEvent(parsingState);
//    	throw re;
//    }
//    
//    memoizerMissEvent(parsingState);
//
//    int startingIndex = parsingState.lexerIndex;
//
//    try {
//      AstNode astNode = matchWorker(parsingState);
//      if (astNode != null) {
//        astNode.setFromIndex(startingIndex);
//        astNode.setToIndex(parsingState.lexerIndex);
//        memoizeAst(parsingState, astNode);
//      }
//
//      exitWithMatchEvent(parsingState, astNode);
//      return astNode;
//    } catch (BacktrackingEvent re) {
//    	if (enableNegativeMemoization) {
//    		memoizeError(parsingState, startingIndex);
//    	}
//      exitWithoutMatchEvent(parsingState);
//      throw re;
//    }
//  }
//
//  private final void memoizeAst(ParsingState parsingState, AstNode astNode) {
//    memoizedAstNodes.put(astNode.getFromIndex(), astNode);
//  }
//  
//  private final void memoizeError(ParsingState parsingState, int startingIndex) {
//  	memoizedErrors.add(startingIndex);
//  }
//
//  private final AstNode getMemoizedAst(ParsingState parsingState) {
//  	if (parsingState.hasPendingLeftRecursion()) return null;
//  	
//  	if (enableNegativeMemoization) {
//	  	if (memoizedErrors.contains(parsingState.lexerIndex)) {
//	  		throw BacktrackingEvent.create();
//	  	}
//  	}
//  	
//    return memoizedAstNodes.get(parsingState.lexerIndex);
//  }
//
//  protected abstract AstNode matchWorker(ParsingState parsingState);
//
//  private final void memoizerHitEvent(ParsingState parsingState) {
//    if (parsingState.parsingEventListeners != null) {
//      for (ParsingEventListener parsingEventListener : parsingState.parsingEventListeners) {
//        parsingEventListener.memoizerHit(this, parsingState);
//      }
//    }
//  }
//
//  private final void memoizerMissEvent(ParsingState parsingState) {
//    if (parsingState.parsingEventListeners != null) {
//      for (ParsingEventListener parsingEventListener : parsingState.parsingEventListeners) {
//        parsingEventListener.memoizerMiss(this, parsingState);
//      }
//    }
//  }
//
//  private final void exitWithMatchEvent(ParsingState parsingState, AstNode astNode) {
//    if (parsingState.parsingEventListeners != null) {
//      if (this instanceof RuleMatcher) {
//        /* Fire the exitWithMatchRule event */
//        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
//          listener.exitWithMatchRule((RuleMatcher)this, parsingState, astNode);
//        }
//      } else {
//        /* Fire the exitWithMatchMatcher event */
//        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
//          listener.exitWithMatchMatcher(this, parsingState, astNode);
//        }
//      }
//    }
//  }
//
//  private final void exitWithoutMatchEvent(ParsingState parsingState) {
//    if (parsingState.parsingEventListeners != null) {
//      if (this instanceof RuleMatcher) {
//        /* Fire the exitWithoutMatchRule event */
//        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
//          listener.exitWithoutMatchRule((RuleMatcher)this, parsingState);
//        }
//      } else {
//        /* Fire the exitWithoutMatchMatcher event */
//        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
//          listener.exitWithoutMatchMatcher(this, parsingState);
//        }
//      }
//    }
//  }
//}
