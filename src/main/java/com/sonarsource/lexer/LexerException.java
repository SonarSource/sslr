/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

public class LexerException extends RuntimeException {

  public LexerException(String message, Exception e) {
    super(message, e);
  }

  public LexerException(String message) {
    super(message);
  }
}
