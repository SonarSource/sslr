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
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public abstract class TokenMatcher extends MemoizedMatcher {

  private final boolean hasToBeSkippedFromAst;

  protected TokenMatcher(boolean hasToBeSkippedFromAst) {
    this.hasToBeSkippedFromAst = hasToBeSkippedFromAst;
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    if (isExpectedToken(parsingState.peekToken(parsingState.lexerIndex, this))) {
      Token token = parsingState.popToken(this);
      if (hasToBeSkippedFromAst) {
        return null;
      } else {
        return new AstNode(token);
      }
    } else {
      throw BacktrackingEvent.create();
    }
  }

  protected abstract boolean isExpectedToken(Token token);

}
