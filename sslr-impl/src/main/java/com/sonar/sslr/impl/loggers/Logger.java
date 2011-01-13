/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.loggers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.matcher.Matcher;

public interface Logger {

  void tryToMatch(Matcher matcher, ParsingState parsingState);

  void hasMatched(Matcher matcher, ParsingState parsingState, AstNode astNode);

  void memoizedAstUsed(Matcher matcher, ParsingState parsingState, AstNode astNode);

}
