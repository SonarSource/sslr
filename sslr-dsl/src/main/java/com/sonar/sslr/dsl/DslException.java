/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

public class DslException extends RuntimeException {

  public DslException(String message) {
    super(message);
  }

  public DslException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
