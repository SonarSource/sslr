/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.util.Stack;

import static com.google.common.base.Preconditions.*;

public class LeftRecursionException extends RuntimeException {

  private static final long serialVersionUID = 4398378231378856922L;
  private final Stack<RuleMatcher> stack;

  public LeftRecursionException(Stack<RuleMatcher> stack) {
    checkNotNull(stack, "stack cannot be null");
    checkArgument(stack.size() >= 2, "stack size must be at least 2");
    checkArgument(stack.get(stack.size() - 1).equals(stack.get(0)), "the first and last rule must be equal");

    this.stack = stack;
  }

}
