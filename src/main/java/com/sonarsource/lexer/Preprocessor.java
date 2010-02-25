/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.lexer;

import java.util.List;

public abstract class Preprocessor {

  public abstract boolean process(Token token, List<Token> tokens);

  public void endOfTokens(List<Token> tokens) {
  }
}
