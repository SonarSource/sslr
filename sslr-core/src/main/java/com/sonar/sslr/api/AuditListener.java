/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

/**
 * An AuditListener must be used to be notified of parsing error or standard exception which might occur when analysing a source code
 */
public interface AuditListener extends RecognitionExceptionListener {

  /**
   * This method is called when an exception different from a parsing error occurs
   */
  public void processException(Exception e);
}
