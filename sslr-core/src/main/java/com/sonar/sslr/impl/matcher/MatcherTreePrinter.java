/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

public class MatcherTreePrinter {

  public static String print(Matcher matcher) {
    return print(matcher, true);
  }

  private static String print(Matcher matcher, boolean expandRule) {
    Matcher[] children = matcher.children;

    StringBuilder result = new StringBuilder(matcher.toString());
    if (isRuleImpl(matcher) && expandRule) {
      result.append(".is");
    }

    if (hasChildren(children) && isNotRuleImplToCollapse(matcher, expandRule)) {
      if (result.length() >= 1 && result.charAt(result.length() - 1) == ')') {
        result.deleteCharAt(result.length() - 1);
        result.append(", ");
      } else {
        result.append("(");
      }

      /* Display the children */
      for (int i = 0; i < children.length; i++) {
        result.append(print(children[i], false));
        if (i < children.length - 1) {
          result.append(", ");
        }
      }

      result.append(")");
    }

    return result.toString();
  }

  private static boolean isNotRuleImplToCollapse(Matcher matcher, boolean expandRule) {
    return !(isRuleImpl(matcher) && !expandRule);
  }

  private static boolean hasChildren(Matcher[] children) {
    return children.length > 0;
  }

  private static boolean isRuleImpl(Matcher matcher) {
    return matcher instanceof RuleMatcher;
  }

}
