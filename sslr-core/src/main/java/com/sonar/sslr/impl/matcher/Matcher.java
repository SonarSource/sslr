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
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.ParsingEventListener;

import javax.annotation.Nullable;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public abstract class Matcher {

  public Matcher[] children;

  protected Matcher(Matcher... children) {
    this.children = children;
  }

  public final boolean isMatching(ParsingState parsingState) {
    return matchToIndex(parsingState) >= 0;
  }

  public final int matchToIndex(ParsingState parsingState) {
    int indexBeforeStarting = parsingState.lexerIndex;

    try {
      match(parsingState);
      return parsingState.lexerIndex;
    } catch (BacktrackingEvent re) {
      return -1;
    } finally {
      parsingState.lexerIndex = indexBeforeStarting;
    }
  }

  /**
   * @return AST node, which was constructed
   * @throws BacktrackingEvent if the match was not successful
   */
  public abstract AstNode match(ParsingState parsingState);

  /**
   * @since 1.14
   */
  protected MatchResult doMatch(ParsingState parsingState) {
    // For forward compatibility
    int startingIndex = parsingState.lexerIndex;
    try {
      AstNode astNode = match(parsingState);
      return MatchResult.succeed(parsingState, startingIndex, astNode);
    } catch (BacktrackingEvent e) {
      return MatchResult.fail(parsingState, startingIndex);
    }
  }

  /**
   * @since 1.14
   */
  protected static final class MatchResult {

    private final AstNode astNode;
    private final int toIndex;

    private static final MatchResult NO_MATCH = new MatchResult(-1, null);

    protected static MatchResult fail(ParsingState parsingState, int startingIndex) {
      parsingState.lexerIndex = startingIndex;
      return NO_MATCH;
    }

    protected static MatchResult succeed(ParsingState parsingState, int startingIndex, @Nullable AstNode astNode) {
      int toIndex = parsingState.lexerIndex;
      if (astNode != null) {
        astNode.setFromIndex(startingIndex);
        astNode.setToIndex(toIndex);
      }
      return new MatchResult(toIndex, astNode);
    }

    private MatchResult(int toIndex, @Nullable AstNode astNode) {
      this.toIndex = toIndex;
      this.astNode = astNode;
    }

    public boolean isMatching() {
      return toIndex >= 0;
    }

    public AstNode getAstNode() {
      return astNode;
    }

    public int getToIndex() {
      return toIndex;
    }

  }

  protected final void enterEvent(ParsingState parsingState) {
    if (parsingState.parsingEventListeners != null) {
      if (this instanceof RuleMatcher) {
        /* Fire the enterRule event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.enterRule((RuleMatcher) this, parsingState);
        }
      } else {
        /* Fire the enterMatcher event */
        for (ParsingEventListener listener : parsingState.parsingEventListeners) {
          listener.enterMatcher(this, parsingState);
        }
      }
    }
  }

  protected void exitWithMatchEvent(ParsingState parsingState, @Nullable AstNode astNode) {
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

  protected void exitWithoutMatchEvent(ParsingState parsingState) {
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
