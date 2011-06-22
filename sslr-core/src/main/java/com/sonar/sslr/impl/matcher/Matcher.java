/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.BacktrackingException;

public abstract class Matcher {

  protected Matcher[] children;

  protected Matcher(Matcher... children) {
    this.children = children;
  }

  public Matcher[] getChildren() {
    return this.children;
  }

  public final boolean isMatching(ParsingState parsingState) {
    return matchToIndex(parsingState) >= 0;
  }

  public final int matchToIndex(ParsingState parsingState) {
    int indexBeforeStarting = parsingState.lexerIndex;
    boolean leftRecursionState = parsingState.hasPendingLeftRecursion();
    try {
      match(parsingState);
      return parsingState.lexerIndex;
    } catch (BacktrackingException e) {
      return -1;
    } finally {
      parsingState.lexerIndex = indexBeforeStarting;
      parsingState.setLeftRecursionState(leftRecursionState);
    }
  }

  public abstract AstNode match(ParsingState parsingState);

}
