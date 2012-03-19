/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Sets;
import com.sonar.sslr.impl.matcher.*;

import java.util.Set;
import java.util.Stack;

import static com.sonar.sslr.impl.analysis.EmptyVisitor.*;

public class FirstVisitor {

  private FirstVisitor() {
  }

  public static Set<Matcher> first(Matcher matcher) {
    return first(matcher, new Stack<RuleMatcher>());
  }

  private static Set<Matcher> first(Matcher matcher, Stack<RuleMatcher> stack) {
    if (matcher instanceof TokenMatcher) {
      return first((TokenMatcher) matcher, stack);
    } else if (matcher instanceof OrMatcher) {
      return first((OrMatcher) matcher, stack);
    } else if (matcher instanceof AndMatcher) {
      return first((AndMatcher) matcher, stack);
    } else if (matcher instanceof OptMatcher) {
      return first((OptMatcher) matcher, stack);
    } else if (matcher instanceof OneToNMatcher) {
      return first((OneToNMatcher) matcher, stack);
    } else if (matcher instanceof RuleMatcher) {
      return first((RuleMatcher) matcher, stack);
    } else {
      throw new IllegalArgumentException("The matcher type \"" + matcher.getClass().getSimpleName() + "\" is not supported");
    }
  }

  public static Set<Matcher> first(TokenMatcher matcher, Stack<RuleMatcher> stack) {
    return Sets.newHashSet((Matcher) matcher);
  }

  public static Set<Matcher> first(OrMatcher matcher, Stack<RuleMatcher> stack) {
    Set<Matcher> set = Sets.newHashSet();

    for (Matcher child : matcher.children) {
      set.addAll(first(child, stack));
    }

    return set;
  }

  public static Set<Matcher> first(AndMatcher matcher, Stack<RuleMatcher> stack) {
    Set<Matcher> set = Sets.newHashSet();

    for (Matcher child : matcher.children) {
      set.addAll(first(child, stack));
      if (!empty(child)) {
        break;
      }
    }

    return set;
  }

  public static Set<Matcher> first(OptMatcher matcher, Stack<RuleMatcher> stack) {
    return first(matcher.children[0], stack);
  }

  public static Set<Matcher> first(OneToNMatcher matcher, Stack<RuleMatcher> stack) {
    return first(matcher.children[0], stack);
  }

  public static Set<Matcher> first(RuleMatcher matcher, Stack<RuleMatcher> stack) {
    if (stack.contains(matcher)) {
      stack.push(matcher);
      throw new LeftRecursionException(stack);
    }

    stack.push(matcher);
    Set<Matcher> set = first(matcher.children[0], stack);
    stack.pop();

    return set;
  }

}
