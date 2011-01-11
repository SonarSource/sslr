/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.RecognitionExceptionImpl;

public class AnyTokenButNotMatcher extends Matcher {

  private Matcher matcher;

  public AnyTokenButNotMatcher(Matcher matcher) {
    this.matcher = matcher;
  }

  public AstNode match(ParsingState parsingState) {
    if (matcher.isMatching(parsingState)) {
      throw RecognitionExceptionImpl.create();
    } else {
      return new AstNode(parsingState.popToken(this));
    }
  }

  @Override
  public void setParentRule(RuleImpl parentRule) {
    this.parentRule = parentRule;
    matcher.setParentRule(parentRule);
  }

  public String toString() {
    return "(" + matcher + ")!";
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
