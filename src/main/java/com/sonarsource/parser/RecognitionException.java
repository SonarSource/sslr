/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
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
