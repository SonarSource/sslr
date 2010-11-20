/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

public class LexerException extends RuntimeException {

  public LexerException(String message, Throwable e) {
    super(message, e);
  }

  public LexerException(String message) {
    super(message);
  }
}
