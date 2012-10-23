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

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class AnyTokenButNotMatcher extends StandardMatcher {

  protected AnyTokenButNotMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    enterEvent(parsingState);
    if (!parsingState.hasNextToken()) {
      exitWithoutMatchEvent(parsingState);
      return MatchResult.fail(parsingState, parsingState.lexerIndex);
    }
    int startingIndex = parsingState.lexerIndex;
    if (super.children[0].doMatch(parsingState).isMatching()) {
      exitWithoutMatchEvent(parsingState);
      return MatchResult.fail(parsingState, startingIndex);
    } else {
      AstNode astNode = new AstNode(parsingState.popToken(this));
      exitWithMatchEvent(parsingState, astNode);
      return MatchResult.succeed(parsingState, startingIndex, astNode);
    }
  }

  @Override
  public String toString() {
    return "anyTokenButNot";
  }

}
