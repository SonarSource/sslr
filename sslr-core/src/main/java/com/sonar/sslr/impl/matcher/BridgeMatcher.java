/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public class BridgeMatcher extends MemoizedMatcher {

  private final TokenType from;
  private final TokenType to;

  protected BridgeMatcher(TokenType from, TokenType to) {
    super();

    this.from = from;
    this.to = to;
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
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
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + (from == null ? 0 : from.hashCode());
    result = prime * result + (to == null ? 0 : to.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BridgeMatcher other = (BridgeMatcher) obj;
    if (from == null) {
      if (other.from != null) {
        return false;
      }
    } else if ( !from.equals(other.from)) {
      return false;
    }
    if (to == null) {
      if (other.to != null) {
        return false;
      }
    } else if ( !to.equals(other.to)) {
      return false;
    }
    return true;
  }

}
