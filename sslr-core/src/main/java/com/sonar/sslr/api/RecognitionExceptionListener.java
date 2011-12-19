/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

/**
 * A RecognitionExceptionListener must be used to be notified of parsing error
 */
public interface RecognitionExceptionListener {

  /**
   * This method is called when a recognition exception occurs
   */
  void processRecognitionException(RecognitionException e);
}