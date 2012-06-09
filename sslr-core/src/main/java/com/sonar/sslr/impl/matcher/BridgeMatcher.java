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

import com.google.common.base.Objects;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public final class BridgeMatcher extends MemoizedMatcher {

  private final TokenType from;
  private final TokenType to;

  protected BridgeMatcher(TokenType from, TokenType to) {
    super();

    this.from = from;
    this.to = to;
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    Token token = parsingState.peekToken(parsingState.lexerIndex, this);
    if (from == token.getType()) {
      AstNode astNode = new AstNode(null, "bridgeMatcher", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
      int bridgeLevel = 0;
      do {
        token = parsingState.popToken(this);
        astNode.addChild(new AstNode(token));

        if (token.getType() == from) {
          bridgeLevel++;
        }
        if (token.getType() == to) {
          bridgeLevel--;
        }
      } while (token.getType() != to || bridgeLevel != 0);
      return astNode;
    } else {
      throw BacktrackingEvent.create();
    }
  }

  @Override
  public String toString() {
    return "bridge(" + from.getName() + ", " + to.getName() + ")";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getClass(), from, to);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    BridgeMatcher other = (BridgeMatcher) obj;
    return Objects.equal(this.from, other.from)
        && Objects.equal(this.to, other.to);
  }

}
