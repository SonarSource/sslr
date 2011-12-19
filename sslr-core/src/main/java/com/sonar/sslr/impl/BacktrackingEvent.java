/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

public final class BacktrackingEvent extends RuntimeException {

  private static final long serialVersionUID = 9043689248001323911L;

  private static BacktrackingEvent exception = new BacktrackingEvent();

  private BacktrackingEvent() {
  }

  public static BacktrackingEvent create() {
    return exception;
  }
}
