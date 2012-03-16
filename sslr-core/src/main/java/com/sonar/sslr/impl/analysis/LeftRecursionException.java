/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.util.Stack;

public class LeftRecursionException extends RuntimeException {

  private final Stack<RuleMatcher> stack;

  public LeftRecursionException(Stack<RuleMatcher> stack) {
    this.stack = stack;
  }

}
