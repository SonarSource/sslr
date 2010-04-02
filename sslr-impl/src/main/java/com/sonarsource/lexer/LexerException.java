/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
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
