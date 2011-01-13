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

  private static Logger logger;

  static {
    String sslrMode = System.getProperty(SSLR_MODE_PROPERTY);
    if (SSLR_DEBUG_MODE.equals(sslrMode)) {
      createParserLogger(new DebugLogger());
    } else {
      logger = new EmptyLogger();
    }
  }

  protected static void createParserLogger(Logger newLogger) {
    logger = newLogger;
  }

  public static void tryToMatch(Matcher matcher, ParsingState parsingState) {
    logger.tryToMatch(matcher, parsingState);
  }

  public static void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.hasMatched(matcher, parsingState, astNode);
  }

  public static void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.memoizedAstUsed(matcher, parsingState, astNode);

  }

}
