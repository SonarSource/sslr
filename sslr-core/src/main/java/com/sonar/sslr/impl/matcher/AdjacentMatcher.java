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
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

public final class AdjacentMatcher extends StandardMatcher {

  protected AdjacentMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    enterEvent(parsingState);
    MatchResult matchResult = memoizerLookup(parsingState);
    if (matchResult != null) {
      return matchResult;
    }
    int index = parsingState.lexerIndex;
    Token nextToken = parsingState.peekTokenIfExists(index, this);
    Token previousToken = parsingState.readToken(index - 1);
    if (nextToken != null
        && nextToken.getColumn() <= previousToken.getColumn() + previousToken.getValue().length()
        && nextToken.getLine() == previousToken.getLine()) {
      matchResult = super.children[0].doMatch(parsingState);
      if (matchResult.isMatching()) {
        AstNode node = new AstNode(null, "adjacentMatcher", nextToken);
        node.addChild(matchResult.getAstNode());
        exitWithMatchEvent(parsingState, node);
        return memoize(parsingState, MatchResult.succeed(parsingState, index, node));
      } else {
        exitWithoutMatchEvent(parsingState);
        return MatchResult.fail(parsingState, index);
      }
    } else {
      exitWithoutMatchEvent(parsingState);
      return MatchResult.fail(parsingState, index);
    }
  }

  @Override
  public String toString() {
    return "adjacent";
  }

}
