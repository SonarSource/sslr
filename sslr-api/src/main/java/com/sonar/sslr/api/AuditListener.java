/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

public interface AuditListener {

  public void addRecognitionException(RecognitionException e);

  public void addException(Exception e);
}
