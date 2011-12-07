/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

public class ExclusiveTillMatcher extends StatelessMatcher {

  protected ExclusiveTillMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    Token nextToken = parsingState.peekTokenIfExists(parsingState.lexerIndex, this);

    AstNode astNode = new AstNode(null, "exclusiveTillMatcher", nextToken);
    while (nothingMatch(parsingState)) {
      Token token = parsingState.popToken(this);
      astNode.addChild(new AstNode(token));
    }

    return astNode;
  }

  private final boolean nothingMatch(ParsingState parsingState) {
    for (Matcher matcher : super.children) {
      if (matcher.isMatching(parsingState)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "exclusiveTill";
  }

}
