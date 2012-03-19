/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.RuleMatcher;
import org.apache.commons.io.IOUtils;

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

  public String getRulesStackTrace() {
    StringBuilder sb = new StringBuilder();

    for (RuleMatcher rule : stack) {
      sb.append("\t");
      sb.append("called by ");
      sb.append(rule.getName());
      sb.append(IOUtils.LINE_SEPARATOR);
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return "The rule \"" + getLeftRecursiveRule().getName() + "\" contains a left recursion." + IOUtils.LINE_SEPARATOR + getRulesStackTrace();
  }

}
