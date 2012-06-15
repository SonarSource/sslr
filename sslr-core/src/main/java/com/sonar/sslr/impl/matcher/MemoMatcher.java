/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.ParsingEventListener;

/**
 * Special wrapping {@link Matcher} that performs memoization of its submatcher.
 *
 * @since 1.14
 */
public class MemoMatcher extends DelegatingMatcher {

  public MemoMatcher(Matcher delegate) {
    super(delegate);
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    AstNode memoizedAstNode = parsingState.getMemoizedAst(this);
    if (memoizedAstNode != null) {
      enterEvent(getDelegate(), parsingState);
      memoizerHitEvent(parsingState);
      parsingState.lexerIndex = memoizedAstNode.getToIndex();
      exitWithMatchEvent(getDelegate(), parsingState, memoizedAstNode);
      return MatchResult.succeed(parsingState, memoizedAstNode.getFromIndex(), memoizedAstNode);
    } else {
      memoizerMissEvent(parsingState);
      MatchResult matchResult = super.doMatch(parsingState);
      if (matchResult.getAstNode() != null) {
        parsingState.memoizeAst(this, matchResult.getAstNode());
      }
      return matchResult;
    }
  }

  private static void enterEvent(Matcher matcher, ParsingState parsingState) {
    if (parsingState.parsingEventListeners != null) {
      if (matcher instanceof RuleMatcher) {
        /* Fire the enterRule event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.enterRule((RuleMatcher) matcher, parsingState);
        }
      } else {
        /* Fire the enterMatcher event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.enterMatcher(matcher, parsingState);
        }
      }
    }
  }

  private static void exitWithMatchEvent(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    if (parsingState.parsingEventListeners != null) {
      if (matcher instanceof RuleMatcher) {
        /* Fire the exitWithMatchRule event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithMatchRule((RuleMatcher) matcher, parsingState, astNode);
        }
      } else {
        /* Fire the exitWithMatchMatcher event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.exitWithMatchMatcher(matcher, parsingState, astNode);
        }
      }
    }
  }

}
