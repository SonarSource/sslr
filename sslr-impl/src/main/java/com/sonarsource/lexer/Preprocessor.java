/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.lexer;

import java.util.List;

public abstract class Preprocessor {

  public abstract boolean process(Token token, List<Token> tokens);

  public void endLexing(List<Token> tokens) {
  }

  public void startLexing() {
  }
}
