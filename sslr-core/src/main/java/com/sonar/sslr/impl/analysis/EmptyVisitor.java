/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.sonar.sslr.impl.matcher.*;

import java.util.Stack;

public class EmptyVisitor {

  private EmptyVisitor() {
  }

  public static boolean empty(Matcher matcher) {
    MatcherVisitor<Boolean, Stack<RuleMatcher>> visitor = new MatcherVisitor<Boolean, Stack<RuleMatcher>>() {

      @Override
      public Boolean visit(TokenMatcher matcher, Stack<RuleMatcher> stack) {
        return false;
      }

      @Override
      public Boolean visit(OrMatcher matcher, Stack<RuleMatcher> stack) {
        for (Matcher childMatcher : matcher.children) {
          if (visit(childMatcher, stack)) {
            return true;
          }
        }

        return false;
      }

      @Override
      public Boolean visit(AndMatcher matcher, Stack<RuleMatcher> stack) {
        for (Matcher childMatcher : matcher.children) {
          if (!visit(childMatcher, stack)) {
            return false;
          }
        }

        return true;
      }

      @Override
      public Boolean visit(OptMatcher matcher, Stack<RuleMatcher> stack) {
        return true;
      }

      @Override
      public Boolean visit(OneToNMatcher matcher, Stack<RuleMatcher> stack) {
        return visit(matcher.children[0], stack);
      }

      @Override
      public Boolean visit(RuleMatcher matcher, Stack<RuleMatcher> stack) {
        if (stack.contains(matcher)) {
          stack.push(matcher);
          throw new LeftRecursionException(stack);
        }

        stack.push(matcher);
        boolean empty = visit(matcher.children[0], stack);
        stack.pop();

        return empty;
      }

    };

    return visitor.visit(matcher, new Stack<RuleMatcher>());
  }

}
