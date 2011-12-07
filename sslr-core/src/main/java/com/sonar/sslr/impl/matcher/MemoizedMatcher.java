package com.sonar.sslr.impl.matcher;

/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

// Initial memoizer strategy (1, from SSLR 1.4)

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.ParsingEventListener;

public abstract class MemoizedMatcher extends Matcher {

  public MemoizedMatcher(Matcher... children) {
    super(children);
  }

  @Override
  public final AstNode match(ParsingState parsingState) {
    enterEvent(parsingState);

    /* Memoizer lookup */
    AstNode memoizedAstNode = getMemoizedAst(parsingState);
    if (memoizedAstNode != null) {
      memoizerHitEvent(parsingState);
      parsingState.lexerIndex = memoizedAstNode.getToIndex();
      exitWithMatchEvent(parsingState, memoizedAstNode);
      return memoizedAstNode;
    }
    memoizerMissEvent(parsingState);

    int startingIndex = parsingState.lexerIndex;

    try {
      AstNode astNode = matchWorker(parsingState);
      if (astNode != null) {
        astNode.setFromIndex(startingIndex);
        astNode.setToIndex(parsingState.lexerIndex);
        memoizeAst(parsingState, astNode);
      }

      exitWithMatchEvent(parsingState, astNode);
      return astNode;
    } catch (BacktrackingEvent re) {
      exitWithoutMatchEvent(parsingState);
      throw re;
    }
  }

  private final void memoizeAst(ParsingState parsingState, AstNode astNode) {
    parsingState.memoizeAst(this, astNode);
  }

  private final AstNode getMemoizedAst(ParsingState parsingState) {
    return parsingState.getMemoizedAst(this);
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

  private final void exitWithoutMatchEvent(ParsingState parsingState) {
    if (parsingState.parsingEventListeners != null) {
      if (this instanceof RuleMatcher) {
        /* Fire the exitWithoutMatchRule event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithoutMatchRule((RuleMatcher) this, parsingState);
        }
      } else {
        /* Fire the exitWithoutMatchMatcher event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithoutMatchMatcher(this, parsingState);
        }
      }
    }
  }
}
