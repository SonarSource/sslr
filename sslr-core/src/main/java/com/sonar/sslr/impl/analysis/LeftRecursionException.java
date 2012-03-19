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

    RuleMatcher leftRecursiveRule = stack.get(stack.size() - 1);
    int i;
    for (i = 0; i < stack.size() - 1; i++) {
      if (leftRecursiveRule.equals(stack.get(i))) {
        break;
      }
    }
    checkArgument(i < stack.size() - 1, "the latest added rule \"" + leftRecursiveRule.getName() + "\" should appear twice in the stack");

    this.stack = stack;
  }

  public RuleMatcher getLeftRecursiveRule() {
    return stack.get(stack.size() - 1);
  }

  @Override
  public String toString() {
    return super.toString();
  }

}
