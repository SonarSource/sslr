/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser;

public class RecognitionException extends RuntimeException {

  private static RecognitionException exception = new RecognitionException();

  private RecognitionException() {
  }

  public RecognitionException(String message) {
    super(message);
  }

  public static RecognitionException create() {
    return exception;
  }
}
