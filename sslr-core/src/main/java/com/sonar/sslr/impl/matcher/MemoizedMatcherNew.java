/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

//package com.sonar.sslr.impl.matcher;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.sonar.sslr.api.AstNode;
//import com.sonar.sslr.impl.BacktrackingEvent;
//import com.sonar.sslr.impl.ParsingState;
//import com.sonar.sslr.impl.events.ParsingEventListener;
//
//public abstract class MemoizedMatcherNew extends Matcher {
//
//	//private AstNode memoizedAstNodes[] = new AstNode[10000];
//	private Map<Integer, AstNode> memoizedAstNodesa = new HashMap<Integer, AstNode>();
//	
//  @Override
//  public void reinitialize() {
//  	memoizedAstNodesa.clear();
//  }
//	
//  public MemoizedMatcherNew(Matcher... children) {
//    super(children);
//  }
//
//  public final AstNode match(ParsingState parsingState) {
//    enterEvent(parsingState);
//
//    /* Memoizer lookup */
//    AstNode memoizedAstNode = getMemoizedAst(parsingState);
//    if (memoizedAstNode != null) {
//    	//System.out.println("hit " + memoizedAstNode.getToken().getValue() + " by " + MatcherTreePrinter.print(this) + " at lexer index " + parsingState.lexerIndex);
//      memoizerHitEvent(parsingState);
//      parsingState.lexerIndex = memoizedAstNode.getToIndex();
//      exitWithMatchEvent(parsingState, memoizedAstNode);
//      return memoizedAstNode;
//    }
//    memoizerMissEvent(parsingState);
//
//    int startingIndex = parsingState.lexerIndex;
//
//    try {
//      AstNode astNode = matchWorker(parsingState);
//      if (astNode != null) {
//        astNode.setFromIndex(startingIndex);
//        astNode.setToIndex(parsingState.lexerIndex);
//        //System.out.println("add " + astNode.getToken().getValue() + " by " + MatcherTreePrinter.print(this) + " at lexer index " + startingIndex);
//        memoizeAst(parsingState, astNode);
//      }
//
//      exitWithMatchEvent(parsingState, astNode);
//      return astNode;
//    } catch (BacktrackingEvent re) {
//      exitWithoutMatchEvent(parsingState);
//      throw re;
//    }
//  }
//
//  private final void memoizeAst(ParsingState parsingState, AstNode astNode) {
//    memoizedAstNodesa.put(astNode.getFromIndex(), astNode);
//  }
//
//  private final AstNode getMemoizedAst(ParsingState parsingState) {
//  	if (parsingState.hasPendingLeftRecursion()) return null;
//  	
//    return memoizedAstNodesa.get(parsingState.lexerIndex);
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
