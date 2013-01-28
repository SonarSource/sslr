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

import com.sonar.sslr.impl.ParsingState;

/**
 * A special {@link Matcher} not actually matching any input but rather trying its submatcher against the current input
 * position. Succeeds if the submatcher would fail.
 *
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class NotMatcher extends StandardMatcher {

  public NotMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    // Note that memoization not used here, because anyway doesn't work for match failures and for null AstNodes
    enterEvent(parsingState);
    int startingIndex = parsingState.lexerIndex;
    MatchResult matchResult = super.children[0].doMatch(parsingState);
    if (matchResult.isMatching()) {
      parsingState.lexerIndex = startingIndex;
      exitWithoutMatchEvent(parsingState);
      return MatchResult.fail(parsingState, startingIndex);
    } else {
      exitWithMatchEvent(parsingState, null);
      return MatchResult.succeed(parsingState, startingIndex, null);
    }
  }

  @Override
  public String toString() {
    return "not";
  }

}
