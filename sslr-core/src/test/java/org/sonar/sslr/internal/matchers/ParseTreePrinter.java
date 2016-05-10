/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.sslr.internal.matchers;

import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.vm.TokenExpression;

public class ParseTreePrinter {

  public static String leafsToString(ParseNode node, char[] input) {
    StringBuilder result = new StringBuilder();
    printLeafs(node, input, result);
    return result.toString();
  }

  private static void printLeafs(ParseNode node, char[] input, StringBuilder result) {
    if (node.getChildren().isEmpty()) {
      for (int i = node.getStartIndex(); i < Math.min(node.getEndIndex(), input.length); i++) {
        result.append(input[i]);
      }
    } else {
      for (ParseNode child : node.getChildren()) {
        printLeafs(child, input, result);
      }
    }
  }

  public static void print(ParseNode node, char[] input) {
    print(node, 0, input);
  }

  private static void print(ParseNode node, int level, char[] input) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    StringBuilder sb = new StringBuilder();
    for (int i = node.getStartIndex(); i < Math.min(node.getEndIndex(), input.length); i++) {
      sb.append(input[i]);
    }
    System.out.println(matcherToString(node.getMatcher())
        + " (start=" + node.getStartIndex()
        + ", end=" + node.getEndIndex()
        + ", matches=" + sb.toString()
        + ")");
    for (ParseNode child : node.getChildren()) {
      print(child, level + 1, input);
    }
  }

  private static String matcherToString(Matcher matcher) {
    if (matcher instanceof MutableParsingRule) {
      return ((MutableParsingRule) matcher).getName();
    } else if (matcher instanceof TokenExpression) {
      return ((TokenExpression) matcher).getTokenType().getName();
    } else {
      return matcher.toString();
    }
  }

}
