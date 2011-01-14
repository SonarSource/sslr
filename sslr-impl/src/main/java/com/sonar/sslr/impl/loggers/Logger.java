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
 * Classes that implement this interface can be called by the {@link ParserLogger} class to treat parsing information.
 */
public interface Logger {

  /**
   * Fires the event that a matcher is trying to match a token at a given index.
   * 
   * @param matcher
   *          the matcher that is trying to match
   * @param parsingState
   *          the state to can give information about the token and its position
   */
  void tryToMatch(Matcher matcher, ParsingState parsingState);

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
  void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode);

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
  void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode);

}
