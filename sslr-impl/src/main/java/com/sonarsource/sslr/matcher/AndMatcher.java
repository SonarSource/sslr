/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.matcher;

import com.sonarsource.sslr.ParsingState;
import com.sonarsource.sslr.api.AstNode;

public class AndMatcher extends Matcher {

  private final Matcher[] matchers;

  public AndMatcher(Matcher... matchers) {
    this.matchers = matchers;
  }

  public AstNode match(ParsingState parsingState) {
    AstNode[] childNodes = new AstNode[matchers.length];
    int startIndex = parsingState.lexerIndex;

    for (int i = 0; i < matchers.length; i++) {
      childNodes[i] = matchers[i].match(parsingState);
    }

    AstNode astNode = new AstNode(this, "AllMatcher", parsingState.peekTokenIfExists(startIndex, this));
    for (int i = 0; i < childNodes.length; i++) {
      astNode.addChild(childNodes[i]);
    }
    return astNode;
  }

  public String toString() {
    StringBuilder expr = new StringBuilder();
    for (int i = 0; i < matchers.length; i++) {
      expr.append(matchers[i]);
      if (i < matchers.length - 1) {
        expr.append(" ");
      }
    }
    return expr.toString();
  }

  @Override
  public void setParentRule(Rule parentRule) {
    this.parentRule = parentRule;
    for (Matcher matcher : matchers) {
      matcher.setParentRule(parentRule);
    }
  }
}
