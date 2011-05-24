/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import java.util.HashSet;

import com.sonar.sslr.impl.events.MatcherAdapter;
import com.sonar.sslr.impl.events.RuleImplAdapter;

public class MatcherTreePrinter {

  private static HashSet<String> implicitAnd = new HashSet<String>() {

    {
      OptMatcher.class.getCanonicalName();
      NextMatcher.class.getCanonicalName();
      ZeroToNMatcher.class.getCanonicalName();
      OneToNMatcher.class.getCanonicalName();
    }
  };

  public static String print(Matcher matcher) {
    return print(matcher, true, false);
  }

  public static String printWithAdapters(Matcher matcher) {
    return print(matcher, true, true);
  }

  private static String print(Matcher matcher, boolean expandRule, boolean showAdapters) {
    Matcher[] children = matcher.getChildren();
    StringBuilder result = new StringBuilder(matcher.toString());

    if (isAdapter(matcher) && !showAdapters) {
      /* Skip this adapter matcher */
      return print(children[0], true, showAdapters);
    }

    if (isRule(matcher) && expandRule) {
      result.append(".is");
    }

    if (hasChildren(children) && !isRuleToBeCollapsed(matcher, expandRule)) {
      result.append("(");

      if (hasOnlyOneAndMatcherWhichHasOnlyOneChild(matcher)) {
        /* Remove the implicitly added and matcher (which contains only a single child) */
        result.append(print(matcher.children[0].children[0], false, showAdapters));
      } else {
        /* Display the children */
        for (int i = 0; i < children.length; i++) {
          result.append(print(children[i], false, showAdapters));
          if (i < children.length - 1) {
            result.append(", ");
          }
        }
      }

      result.append(")");
    }

    return result.toString();
  }

  private static boolean hasOnlyOneAndMatcherWhichHasOnlyOneChild(Matcher matcher) {
    Matcher[] children = matcher.getChildren();
    return children.length == 1 && children[0] instanceof AndMatcher && children[0].getChildren().length == 1
        && implicitAnd.contains(matcher.getClass().getCanonicalName());
  }

  private static boolean hasChildren(Matcher[] children) {
    return children.length > 0;
  }

  private static boolean isAdapter(Matcher matcher) {
    return matcher instanceof MemoizerMatcher || matcher instanceof RuleImplAdapter || matcher instanceof MatcherAdapter;
  }

  private static boolean isRule(Matcher matcher) {
    return !(matcher instanceof RuleImplAdapter) && matcher instanceof RuleImpl;
  }

  private static boolean isRuleToBeCollapsed(Matcher matcher, boolean expandRule) {
    return isRule(matcher) && !expandRule;
  }

}
