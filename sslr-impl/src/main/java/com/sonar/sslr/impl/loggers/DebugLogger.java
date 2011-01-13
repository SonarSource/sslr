/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.loggers;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

class DebugLogger implements Logger {

  static final ch.qos.logback.classic.Logger consoleLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DebugLogger.class);

  static {
    PatternLayout patternLayout = new PatternLayout();
    patternLayout.setPattern("%-5level - %msg%n");
    ConsoleAppender<LoggingEvent> appender = new ConsoleAppender<LoggingEvent>();
    appender.setName("DebugLoggerAppender");
    appender.setLayout(patternLayout);
    consoleLogger.setLevel(Level.INFO);
    consoleLogger.addAppender(appender);
    // consoleLogger.setAdditive(false);
  }

  public void tryToMatch(Matcher matcher, ParsingState parsingState) {
    consoleLogger.info("... Matcher \"" + matcher + "\" tries to match token #" + parsingState.lexerIndex + ": \""
        + parsingState.readToken(parsingState.lexerIndex) + "\"");
  }

  public void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    consoleLogger.info("==> Matcher \"" + matcher + "\" has matched \"" + parsingState.readToken(parsingState.lexerIndex)
        + "\" and returns the following AST: \t" + astNode);
  }

  public void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    consoleLogger.info("\t\t/!\\ Memoized cache used by matcher \"" + matcher + "\" for token "
        + parsingState.readToken(parsingState.lexerIndex) + " (at position " + parsingState.lexerIndex
        + ") and has returned the following AST: \t" + astNode);
  }

}
