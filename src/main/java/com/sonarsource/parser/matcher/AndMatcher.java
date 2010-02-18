/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

import com.sonarsource.parser.ParsingState;
import com.sonarsource.parser.ast.AstNode;

public class AndMatcher extends Matcher {

  private final Matcher[] matchers;
  private final AstNode[] childNodes;

  public AndMatcher(Matcher... matchers) {
    this.matchers = matchers;
    childNodes = new AstNode[matchers.length];
  }

  public AstNode match(ParsingState parsingState) {
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
