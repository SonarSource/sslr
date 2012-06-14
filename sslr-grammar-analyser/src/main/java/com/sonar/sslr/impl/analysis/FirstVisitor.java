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

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.*;

import java.util.Set;
import java.util.Stack;

import static com.sonar.sslr.impl.analysis.EmptyVisitor.*;

public final class FirstVisitor {

  private FirstVisitor() {
  }

  public static Set<Matcher> first(Matcher matcher) {
    MatcherVisitor<Set<Matcher>, Stack<RuleMatcher>> visitor = new MatcherVisitor<Set<Matcher>, Stack<RuleMatcher>>() {

      @Override
      public Set<Matcher> visit(TokenMatcher matcher, Stack<RuleMatcher> stack) {
        return Sets.newHashSet((Matcher) matcher);
      }

      @Override
      public Set<Matcher> visit(OrMatcher matcher, Stack<RuleMatcher> stack) {
        Set<Matcher> set = Sets.newHashSet();

        for (Matcher child : matcher.children) {
          set.addAll(visit(child, stack));
        }

        return set;
      }

      @Override
      public Set<Matcher> visit(AndMatcher matcher, Stack<RuleMatcher> stack) {
        Set<Matcher> set = Sets.newHashSet();

        for (Matcher child : matcher.children) {
          set.addAll(visit(child, stack));
          if (!empty(child)) {
            break;
          }
        }

        return set;
      }

      @Override
      public Set<Matcher> visit(OptMatcher matcher, Stack<RuleMatcher> stack) {
        return visit(matcher.children[0], stack);
      }

      @Override
      public Set<Matcher> visit(OneToNMatcher matcher, Stack<RuleMatcher> stack) {
        return visit(matcher.children[0], stack);
      }

      @Override
      public Set<Matcher> visit(RuleMatcher matcher, Stack<RuleMatcher> stack) {
        if (stack.contains(matcher)) {
          stack.push(matcher);
          throw new LeftRecursionException(stack);
        }

        stack.push(matcher);
        Set<Matcher> set = visit(matcher.children[0], stack);
        stack.pop();

        return set;
      }

    };

    return visitor.visit(matcher, new Stack<RuleMatcher>());
  }

}
