/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class AdjacentMatcher extends Matcher {

  private final Matcher matcher;

  public AdjacentMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    int index = parsingState.lexerIndex;
    Token nextToken = parsingState.peekToken(index, this);
    Token previousToken = parsingState.readToken(index - 1);
    if (nextToken.getColumn() <= previousToken.getColumn() + previousToken.getValue().length()) {
      AstNode node = new AstNode(this, "adjacentMatcher", nextToken);
      node.addChild(matcher.match(parsingState));
      return node;
    } else {
      throw RecognitionExceptionImpl.create();
    }
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    matcher.parentRule = parentRule;
  }

  @Override
  public String toString() {
    StringBuilder expr = new StringBuilder("(");
    expr.append(matcher);
    expr.append(")adjacent");
    return expr.toString();
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
}
