/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;

public class BooleanMatcher extends MemoizedMatcher {

  private final boolean internalState;

  protected BooleanMatcher(boolean internalState) {
    super();

    this.internalState = internalState;
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    parsingState.peekToken(this);
    if (internalState) {
      return new AstNode(null, "trueMatcher", parsingState.popToken(this));
    } else {
      throw BacktrackingEvent.create();
    }
  }

  @Override
  public String toString() {
    return (internalState) ? "isTrue()" : "isFalse()";
  }

}
