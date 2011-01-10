/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

/**
 * Temporary Exception, should be removed later.
 */
public class LeftRecursionDetectedException extends RuntimeException {

  private static final long serialVersionUID = -482559252202047840L;

  public LeftRecursionDetectedException() {
    super();
  }

  public LeftRecursionDetectedException(String message, Throwable exception) {
    super(message, exception);
  }

  public LeftRecursionDetectedException(String message) {
    super(message);
  }

}
