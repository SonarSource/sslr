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
package com.sonar.sslr.impl.matcher;

public final class MatcherTreePrinter {

  private MatcherTreePrinter() {
  }

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
