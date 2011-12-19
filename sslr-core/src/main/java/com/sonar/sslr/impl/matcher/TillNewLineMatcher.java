/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.api.GenericTokenType.*;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

public final class TillNewLineMatcher extends StatelessMatcher {

  protected TillNewLineMatcher() {
    super();
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    int currentLine = parsingState.lexerIndex - 1 >= 0 ? parsingState.readToken(parsingState.lexerIndex - 1).getLine() : 1;

    AstNode astNode = new AstNode(null, "tillNewLine", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
    for (int i = parsingState.lexerIndex; i < parsingState.lexerSize && parsingState.peekToken(this).getLine() == currentLine
        && parsingState.peekToken(this).getType() != EOF; i++) {
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
