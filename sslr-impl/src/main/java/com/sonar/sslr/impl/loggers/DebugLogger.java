/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.loggers;

import java.io.PrintStream;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

/**
 * Debug logger that formats and prints out DEBUG information to analyze the parsing.
 */
class DebugLogger implements Logger {

  private PrintStream logger;

  public DebugLogger() {
    logger = System.out;
  }

  /**
   * {@inheritDoc}
   */
  public void tryToMatch(Matcher matcher, ParsingState parsingState) {
    logger.printf("--> %1$-6s %2$-30s %3$s%n", parsingState.lexerIndex, parsingState.readToken(parsingState.lexerIndex), matcher);
  }

  /**
   * {@inheritDoc}
   */
  public void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.printf("<== %1$-6s %2$-30s %3$s%n", parsingState.lexerIndex, parsingState.readToken(parsingState.lexerIndex), matcher, astNode);
  }

  /**
   * {@inheritDoc}
   */
  public void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.printf("< > %1$-6s %2$-30s %3$s%n", parsingState.lexerIndex, parsingState.readToken(parsingState.lexerIndex), matcher, astNode);
  }

}
