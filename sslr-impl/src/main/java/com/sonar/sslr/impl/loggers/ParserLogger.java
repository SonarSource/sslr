/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.loggers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

public final class ParserLogger {

  public static final String SSLR_MODE_PROPERTY = "sonar.sslr.mode";
  public static final String SSLR_DEBUG_MODE = "debug";

  private static ThreadLocal<Logger> logger = new ThreadLocal<Logger>();

  static {
    String sslrMode = System.getProperty(SSLR_MODE_PROPERTY);
    if (SSLR_DEBUG_MODE.equals(sslrMode)) {
      createParserLogger(new DebugLogger());
    } else {
      logger.set(new EmptyLogger());
    }
  }

  protected static void createParserLogger(Logger newLogger) {
    logger.set(newLogger);
  }

  public static void tryToMatch(Matcher matcher, ParsingState parsingState) {
    logger.get().tryToMatch(matcher, parsingState);
  }

  public static void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.get().hasMatched(matcher, parsingState, astNode);
  }

  public static void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.get().memoizedAstUsed(matcher, parsingState, astNode);

  }

}
