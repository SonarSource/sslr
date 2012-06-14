/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
