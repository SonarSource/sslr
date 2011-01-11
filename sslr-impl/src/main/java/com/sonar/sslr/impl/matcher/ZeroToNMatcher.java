/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;

public class ZeroToNMatcher extends Matcher {

  private Matcher matcher;

  public ZeroToNMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    if (parsingState.hasNextToken()) {
      int startIndex = parsingState.lexerIndex;
      AstNode astNode = null;
      boolean match = true;
      do {
        match = matcher.isMatching(parsingState);
        if (match) {
          if (astNode == null) {
            astNode = new AstNode(this, "zeroToNMatcher", parsingState.peekTokenIfExists(startIndex, this));
          }
          astNode.addChild(matcher.match(parsingState));
        }
      } while (match);
      return astNode;
    } else {
      return null;
    }
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startParsing(ParsingState parsingState) {
    matcher.notifyStartParsing(parsingState);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endParsing(ParsingState parsingState) {
    matcher.notifyEndParsing(parsingState);
  }

  public String toString() {
    return "(" + matcher + ")*";
  }
}
