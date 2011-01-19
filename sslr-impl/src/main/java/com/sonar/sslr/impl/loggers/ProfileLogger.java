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
class ProfileLogger implements Logger {

  private PrintStream logger;

  public ProfileLogger() {
    logger = System.out;
  }

  /**
   * {@inheritDoc}
   */
  public void tryToMatch(Matcher matcher, ParsingState parsingState) {
    // TODO
  }

  /**
   * {@inheritDoc}
   */
  public void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    // TODO
  }

  /**
   * {@inheritDoc}
   */
  public void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    // TODO
  }

  public void hasMatchedWithLeftRecursion(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    // TODO
  }

  public void stopLeftRecursion(Matcher matcher, ParsingState parsingState) {
    // TODO
  }

  public void flushLog() {
    // TODO Auto-generated method stub

  }

}
