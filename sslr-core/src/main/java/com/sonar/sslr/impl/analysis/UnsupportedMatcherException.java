/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.Matcher;

import static com.google.common.base.Preconditions.*;

public class UnsupportedMatcherException extends RuntimeException {

  private static final long serialVersionUID = -155411423005566354L;
  private final String matcher;

  public UnsupportedMatcherException(Matcher matcher) {
    checkNotNull(matcher, "matcher cannot be null");

    this.matcher = matcher.getClass().getSimpleName();
  }

  @Override
  public String toString() {
    return "The matcher \"" + matcher + "\" is not supported";
  }

}
