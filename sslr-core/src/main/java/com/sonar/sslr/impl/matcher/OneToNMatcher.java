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
 * A {@link Matcher} that repeatedly tries its submatcher against the input.
 * Succeeds if its submatcher succeeds at least once.
 */
public final class OneToNMatcher extends StandardMatcher {

  protected OneToNMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    enterEvent(parsingState);
    MatchResult matchResult = memoizerLookup(parsingState);
    if (matchResult != null) {
      return matchResult;
    }
    int startIndex = parsingState.lexerIndex;
    AstNode astNode = null;
    int loop = 0;
    do {
      matchResult = super.children[0].doMatch(parsingState);
      if (matchResult.isMatching()) {
        if (astNode == null) {
          astNode = new AstNode(null, "oneToNMatcher", parsingState.peekTokenIfExists(startIndex, this));
        }
        astNode.addChild(matchResult.getAstNode());
        loop++;
      }
    } while (matchResult.isMatching());
    if (loop == 0) {
      exitWithoutMatchEvent(parsingState);
      return MatchResult.fail(parsingState, startIndex);
    }
    exitWithMatchEvent(parsingState, astNode);
    return memoize(parsingState, MatchResult.succeed(parsingState, startIndex, astNode));
  }

  @Override
  public String toString() {
    return "one2n";
  }

}
