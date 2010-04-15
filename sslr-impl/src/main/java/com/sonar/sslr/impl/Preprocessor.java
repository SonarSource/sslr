/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import com.sonar.sslr.api.Token;

public abstract class Preprocessor {

  public abstract boolean process(Token token, LexerOutput output);

  public void endLexing(LexerOutput output) {
  }

  public void startLexing() {
  }
}
