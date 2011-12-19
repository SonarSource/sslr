/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public final class AndMatcher extends StatelessMatcher {

  protected AndMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    AstNode[] childNodes = new AstNode[super.children.length];
    int startIndex = parsingState.lexerIndex;

    for (int i = 0; i < super.children.length; i++) {
      childNodes[i] = super.children[i].match(parsingState);
    }

    AstNode astNode = new AstNode(null, "AllMatcher", parsingState.peekTokenIfExists(startIndex, this));
    for (AstNode childNode : childNodes) {
      astNode.addChild(childNode);
    }
    return astNode;
  }

  @Override
  public String toString() {
    return "and";
  }

}
