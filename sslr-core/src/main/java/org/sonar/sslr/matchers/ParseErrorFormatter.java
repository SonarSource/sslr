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
package org.sonar.sslr.matchers;

import org.sonar.sslr.internal.matchers.*;
import org.sonar.sslr.internal.matchers.InputBuffer.Position;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParseErrorFormatter {

  /**
   * Number of lines in snippet before and after line with error.
   */
  private static final int SNIPPET_SIZE = 10;

  public String format(ParseError parseError) {
    InputBuffer inputBuffer = parseError.getInputBuffer();
    Position position = inputBuffer.getPosition(parseError.getErrorIndex());
    StringBuilder sb = new StringBuilder();
    sb.append("At line ").append(position.getLine())
        .append(" column ").append(position.getColumn())
        .append(' ').append(parseError.getMessage()).append('\n');
    appendSnipped(sb, inputBuffer, position);
    appendFailedPaths(sb, inputBuffer, parseError);
    return sb.toString();
  }

  private static void appendFailedPaths(StringBuilder sb, InputBuffer inputBuffer, ParseError parseError) {
    sb.append("Failed at:\n");

    int splitPoint = findSplitPoint(parseError.getFailedPaths());

    List<List<MatcherPathElement>> paths = parseError.getFailedPaths();
    Collections.sort(paths, new PathComparator());
    traverse(sb, paths, 0, splitPoint - 1, paths.size(), "", true);

    for (int i = splitPoint - 2; i >= 0; i--) {
      MatcherPathElement pathElement = parseError.getFailedPaths().get(0).get(i);
      sb.append(matcherPathElementToString(pathElement)).append('\n');
    }
  }

  private static void traverse(StringBuilder sb, List<List<MatcherPathElement>> lists, int depth, int start, int end, String prefix, boolean isTail) {
    if (depth >= lists.get(start).size()) {
      return;
    }

    boolean tail = true;
    for (int i = start + 1; i < end; i++) {
      if (depth + 1 < lists.get(i).size() &&
          lists.get(i).get(depth + 1) != lists.get(i - 1).get(depth + 1)) {
        traverse(sb, lists, depth + 1, start, i, prefix + (depth == 0 ? "" : isTail ? "  " : "│ " /* \u2502 */), tail);
        start = i;
        tail = false;
      }
    }
    if (start < end) {
      traverse(sb, lists, depth + 1, start, end, prefix + (depth == 0 ? "" : isTail ? "  " : "│ " /* \u2502 */), tail);
    }

    if (depth > 0) {
      sb.append(prefix + (isTail ? "┌─" /* \u250C\u2500 */: "├─" /* \u251C\u2500 */));
    }
    sb.append(matcherPathElementToString(lists.get(start).get(depth))).append('\n');
  }

  private static String matcherPathElementToString(MatcherPathElement pathElement) {
    return ((GrammarElementMatcher) pathElement.getMatcher()).getName();
  }

  private static int findSplitPoint(List<List<MatcherPathElement>> paths) {
    int result = 0;
    while (true) {
      if (result == paths.get(0).size()) {
        return result;
      }
      Matcher matcher = paths.get(0).get(result).getMatcher();
      for (int i = 1; i < paths.size(); i++) {
        if (result == paths.get(i).size()
            || !matcher.equals(paths.get(i).get(result).getMatcher())) {
          return result;
        }
      }
      result++;
    }
  }

  private static void appendSnipped(StringBuilder sb, InputBuffer inputBuffer, Position position) {
    int startLine = Math.max(position.getLine() - SNIPPET_SIZE, 1);
    int endLine = Math.min(position.getLine() + SNIPPET_SIZE, inputBuffer.getLineCount());
    int padding = Integer.toString(endLine).length();
    String lineNumberFormat = "%1$" + padding + "d: ";
    for (int line = startLine; line <= endLine; line++) {
      sb.append(String.format(lineNumberFormat, line));
      sb.append(trimTrailingLineSeparatorFrom(inputBuffer.extractLine(line))).append('\n');
      if (line == position.getLine()) {
        for (int i = 1; i < position.getColumn() + padding + 2; i++) {
          sb.append(' ');
        }
        sb.append("^\n");
      }
    }
  }

  // TODO Godin: can be replaced by com.google.common.base.CharMatcher.anyOf("\n\r").trimTrailingFrom(string)
  private static String trimTrailingLineSeparatorFrom(String string) {
    int last;
    for (last = string.length() - 1; last >= 0; last--) {
      if (string.charAt(last) != '\n' && string.charAt(last) != '\r') {
        break;
      }
    }
    return string.substring(0, last + 1);
  }

  private static class PathComparator implements Comparator<List<MatcherPathElement>> {
    public int compare(List<MatcherPathElement> o1, List<MatcherPathElement> o2) {
      for (int i = 0; i < o1.size(); i++) {
        if (i < o2.size()) {
          if (!o1.get(i).getMatcher().equals(o2.get(i))) {
            // o1: A
            // o2: B
            return -1;
          }
        } else {
          // o1: A, B
          // o2: A
          return -1;
        }
      }
      if (o1.size() == o2.size()) {
        // o1: A
        // o2: A
        return 0;
      } else {
        // o1: A
        // o2: A, B
        return 1;
      }
    }
  }

}
