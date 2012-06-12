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

}
