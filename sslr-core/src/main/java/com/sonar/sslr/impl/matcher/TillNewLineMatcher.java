/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

import static com.sonar.sslr.api.GenericTokenType.EOF;

public class TillNewLineMatcher extends MemoizedMatcher {

  protected TillNewLineMatcher() {
    super();
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    int currentLine = (parsingState.lexerIndex - 1 >= 0) ? parsingState.readToken(parsingState.lexerIndex - 1).getLine() : 1;

    AstNode astNode = new AstNode(null, "tillNewLine", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
    for (int i = parsingState.lexerIndex; i < parsingState.lexerSize && parsingState.peekToken(this).getLine() == currentLine && parsingState.peekToken(this).getType() != EOF; i++) {
    	Token token = parsingState.popToken(this);
      astNode.addChild(new AstNode(token));
    }

    return astNode;
  }

  @Override
  public String toString() {
    return "tillNewLine()";
  }

}
