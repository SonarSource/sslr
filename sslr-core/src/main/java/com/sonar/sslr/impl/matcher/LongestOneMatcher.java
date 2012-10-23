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

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class LongestOneMatcher extends StatelessMatcher {

  protected LongestOneMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    Matcher longestMatcher = null;
    int longestMatchIndex = -1;

    for (Matcher matcher : super.children) {
      int matcherIndex = matcher.matchToIndex(parsingState);
      if (matcherIndex >= 0 && matcherIndex > longestMatchIndex) {
        /* This matcher could parse the input [as well], but for longer than the current longest matcher? */
        longestMatcher = matcher;
        longestMatchIndex = matcherIndex;
      }
    }

    if (longestMatcher != null) {
      return longestMatcher.match(parsingState);
    }

    throw BacktrackingEvent.create();
  }

  @Override
  public String toString() {
    return "longestOne";
  }

}
