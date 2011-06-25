/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;

public class BridgeMatcher extends MemoizedMatcher {

  private final TokenType from;
  private final TokenType to;

  protected BridgeMatcher(TokenType from, TokenType to) {
  	super();
  	
    this.from = from;
    this.to = to;
  }

  @Override
  public AstNode matchWorker(ParsingState parsingState) {
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
      throw BacktrackingException.create();
    }
  }

  @Override
  public String toString() {
  	return "bridge(" + from.getName() + ", " + to.getName() + ")";
  }
  
}
