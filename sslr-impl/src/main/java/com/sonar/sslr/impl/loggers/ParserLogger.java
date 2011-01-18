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
 * Utility class used to log events fired during the parsing. The purpose is to be able to get detail information on the parsing state.<br/>
 * <br/>
 * By default, this class does nothing when its methods are called.<br/>
 * <ul>
 * <li>If the VM property "sonar.sslr.mode=debug" is found, then the events are logged into the console for debugging purposes.</li>
 * </ul>
 * <br/>
 * An evolution of this class would/will be to keep all the fired events in memory to be able to profile, analyze and -in turn- optimize the
 * parsing.
 */
public final class ParserLogger {

  public static final String SSLR_MODE_PROPERTY = "sonar.sslr.mode";
  public static final String SSLR_DEBUG_MODE = "debug";

  private static Logger logger;

  private ParserLogger() {
  }

  static {
    init();
  }

  private static void init() {
    String sslrMode = System.getProperty(SSLR_MODE_PROPERTY);
    if (SSLR_DEBUG_MODE.equals(sslrMode)) {
      createParserLogger(new DebugLogger());
    } else {
      logger = new EmptyLogger();
    }
  }
  
  public static void activateDebugMode(){
    System.setProperty(SSLR_MODE_PROPERTY, SSLR_DEBUG_MODE);
    init();
  }
  
  public static void deactivateDebugMode(){
    System.clearProperty(SSLR_MODE_PROPERTY);
    init();
  }

  protected static void createParserLogger(Logger newLogger) {
    logger = newLogger;
  }

  /**
   * Fires the event that a matcher is trying to match a token at a given index.
   * 
   * @param matcher
   *          the matcher that is trying to match
   * @param parsingState
   *          the state to can give information about the token and its position
   */
  public static void tryToMatch(Matcher matcher, ParsingState parsingState) {
    logger.tryToMatch(matcher, parsingState);
  }

  /**
   * Fires the event that a matcher has matched at a given index, and gives the resulting AST node.
   * 
   * @param matcher
   *          the matcher that has matched
   * @param parsingState
   *          the state to can give information about the token and its position
   * @param astNode
   *          the AST node that is returned from this match
   */
  public static void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.hasMatched(matcher, parsingState, astNode);
  }

  /**
   * Fires the event that an AST node, that was memoized for the given token at the given index, has been used and returned.
   * 
   * @param matcher
   *          the matcher for which the AST node was memoized
   * @param parsingState
   *          the state to can give information about the token and its position
   * @param astNode
   *          the memoized AST node that is returned
   */
  public static void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode) {
    logger.memoizedAstUsed(matcher, parsingState, astNode);

  }

}
