/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;


public abstract class Preprocessor {

  public abstract boolean process(Token token, LexerOutput output);

  public void endLexing(LexerOutput output) {
  }

  public void startLexing() {
  }
}
