/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.loggers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

/**
 * Default logger that does nothing.
 */
class EmptyLogger implements Logger {

  /**
   * {@inheritDoc}
   */
  public void tryToMatch(Matcher matcher, ParsingState parsingState) {
  }

  /**
   * {@inheritDoc}
   */
  public void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
  }

  /**
   * {@inheritDoc}
   */
  public void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
  }

}
