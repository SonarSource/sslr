/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.*;

import java.util.Stack;

public class EmptyVisitor {

  public static boolean empty(Matcher matcher) {
    return empty(matcher, new Stack<RuleMatcher>());
  }

  private static boolean empty(Matcher matcher, Stack<RuleMatcher> stack) {
    if (matcher instanceof TokenMatcher) {
      return empty((TokenMatcher) matcher, stack);
    } else if (matcher instanceof OrMatcher) {
      return empty((OrMatcher) matcher, stack);
    } else if (matcher instanceof AndMatcher) {
      return empty((AndMatcher) matcher, stack);
    } else if (matcher instanceof OptMatcher) {
      return empty((OptMatcher) matcher, stack);
    } else if (matcher instanceof OneToNMatcher) {
      return empty((OneToNMatcher) matcher, stack);
    } else if (matcher instanceof RuleMatcher) {
      return empty((RuleMatcher) matcher, stack);
    } else {
      throw new IllegalArgumentException("The matcher type \"" + matcher.getClass().getSimpleName() + "\" is not supported");
    }
  }

  public static boolean empty(TokenMatcher matcher, Stack<RuleMatcher> stack) {
    return false;
  }

  public static boolean empty(OrMatcher matcher, Stack<RuleMatcher> stack) {
    for (Matcher childMatcher : matcher.children) {
      if (empty(childMatcher)) {
        return true;
      }
    }

    return false;
  }

  public static boolean empty(AndMatcher matcher, Stack<RuleMatcher> stack) {
    for (Matcher childMatcher : matcher.children) {
      if (!empty(childMatcher)) {
        return false;
      }
    }

    return true;
  }

  public static boolean empty(OptMatcher matcher, Stack<RuleMatcher> stack) {
    return true;
  }

  public static boolean empty(OneToNMatcher matcher, Stack<RuleMatcher> stack) {
    return empty(matcher.children[0]);
  }

  public static boolean empty(RuleMatcher matcher, Stack<RuleMatcher> stack) {
    if (stack.contains(matcher)) {
      stack.push(matcher);
      throw new LeftRecursionException(stack);
    }

    stack.push(matcher);
    boolean empty = empty(matcher.children[0], stack);
    stack.pop();

    return empty;
  }

}
