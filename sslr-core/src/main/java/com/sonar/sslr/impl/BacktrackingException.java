/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

public class BacktrackingException extends RuntimeException {

  private static final long serialVersionUID = 9043689248001323911L;

  private static BacktrackingException exception = new BacktrackingException();

  private BacktrackingException() {
  }

  public static BacktrackingException create() {
    return exception;
  }
}
