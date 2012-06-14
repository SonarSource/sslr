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

import com.sonar.sslr.impl.matcher.*;

import java.util.Stack;

public final class EmptyVisitor {

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
