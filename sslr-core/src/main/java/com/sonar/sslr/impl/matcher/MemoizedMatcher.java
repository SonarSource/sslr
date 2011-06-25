/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
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
    } catch (BacktrackingEvent re) {
      exitWithoutMatchEvent(parsingState, re);
      throw re;
    }
  }

  protected abstract AstNode matchWorker(ParsingState parsingState);

  private final void memoizerHitEvent(ParsingState parsingState) {
    if (parsingState.parsingEventListeners != null) {
      for (ParsingEventListener parsingEventListener : parsingState.parsingEventListeners) {
        parsingEventListener.memoizerHit(this, parsingState);
      }
    }
  }

  private final void memoizerMissEvent(ParsingState parsingState) {
    if (parsingState.parsingEventListeners != null) {
      for (ParsingEventListener parsingEventListener : parsingState.parsingEventListeners) {
        parsingEventListener.memoizerMiss(this, parsingState);
      }
    }
  }

  private final void exitWithMatchEvent(ParsingState parsingState, AstNode astNode) {
    if (parsingState.parsingEventListeners != null) {
      if (this instanceof RuleMatcher) {
        /* Fire the exitWithMatchRule event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithMatchRule((RuleMatcher) this, parsingState, astNode);
        }
      } else {
        /* Fire the exitWithMatchMatcher event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithMatchMatcher(this, parsingState, astNode);
        }
      }
    }
  }

  private final void exitWithoutMatchEvent(ParsingState parsingState, BacktrackingEvent re) {
    if (parsingState.parsingEventListeners != null) {
      if (this instanceof RuleMatcher) {
        /* Fire the exitWithoutMatchRule event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithoutMatchRule((RuleMatcher) this, parsingState, re);
        }
      } else {
        /* Fire the exitWithoutMatchMatcher event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithoutMatchMatcher(this, parsingState, re);
        }
      }
    }
  }
}
