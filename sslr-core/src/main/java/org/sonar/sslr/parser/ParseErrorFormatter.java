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
package org.sonar.sslr.parser;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.InputBuffer.Position;
import org.sonar.sslr.internal.matchers.MatcherPathElement;
import org.sonar.sslr.internal.matchers.TextUtils;

import java.util.List;

/**
 * Formats {@link ParseError} to readable form.
 *
 * <p>This class is not intended to be subclassed by clients.</p>
 *
 * @since 1.16
 */
public class ParseErrorFormatter {

  /**
   * Number of lines in snippet before and after line with error.
   */
  private static final int SNIPPET_SIZE = 10;

  private static final int EXCERPT_SIZE = 40;

  public String format(ParseError parseError) {
    Preconditions.checkNotNull(parseError);

    InputBuffer inputBuffer = parseError.getInputBuffer();
    Position position = inputBuffer.getPosition(parseError.getErrorIndex());
    StringBuilder sb = new StringBuilder();
    sb.append("Parse error at line ").append(position.getLine())
        .append(" column ").append(position.getColumn())
        .append(' ').append(parseError.getMessage()).append('\n');
    sb.append('\n');
    appendSnippet(sb, inputBuffer, position);
    sb.append('\n');
    sb.append("Failed at rules:\n");
    ErrorTreeNode tree = buildTree(parseError.getFailedPaths());
    appendTree(sb, inputBuffer, tree);
    return sb.toString();
  }

  private static class ErrorTreeNode {
    MatcherPathElement pathElement;
    List<ErrorTreeNode> children = Lists.newArrayList();
  }

  private void appendTree(StringBuilder sb, InputBuffer inputBuffer, ErrorTreeNode node) {
    List<ErrorTreeNode> nodes = Lists.newArrayList();
    while (node.children.size() == 1) {
      nodes.add(node);
      node = node.children.get(0);
    }
    appendTree(sb, inputBuffer, node, "", true);
    for (int i = nodes.size() - 1; i >= 0; i--) {
      appendPathElement(sb, inputBuffer, nodes.get(i).pathElement);
    }
  }

  private void appendTree(StringBuilder sb, InputBuffer inputBuffer, ErrorTreeNode node, String prefix, boolean isTail) {
    boolean tail = true;
    for (int i = 0; i < node.children.size(); i++) {
      appendTree(sb, inputBuffer, node.children.get(i), prefix + (isTail ? "  " : "| "), tail);
      tail = false;
    }
    sb.append(prefix + (isTail ? "/-" : "+-"));
    appendPathElement(sb, inputBuffer, node.pathElement);
  }

  private ErrorTreeNode buildTree(List<List<MatcherPathElement>> paths) {
    ErrorTreeNode root = new ErrorTreeNode();
    root.pathElement = paths.get(0).get(0);
    for (List<MatcherPathElement> path : paths) {
      addToTree(root, path);
    }
    return root;
  }

  private static void addToTree(ErrorTreeNode root, List<MatcherPathElement> path) {
    ErrorTreeNode current = root;
    int i = 1;
    boolean found = true;
    while (found && i < path.size()) {
      found = false;
      for (ErrorTreeNode child : current.children) {
        if (child.pathElement.equals(path.get(i))) {
          current = child;
          i++;
          found = true;
          break;
        }
      }
    }
    while (i < path.size()) {
      ErrorTreeNode child = new ErrorTreeNode();
      child.pathElement = path.get(i);
      current.children.add(child);
      current = child;
      i++;
    }
  }

  private static void appendPathElement(StringBuilder sb, InputBuffer inputBuffer, MatcherPathElement pathElement) {
    sb.append(((MutableParsingRule) pathElement.getMatcher()).getName());
    if (pathElement.getStartIndex() != pathElement.getEndIndex()) {
      sb.append(" consumed from ")
          .append(inputBuffer.getPosition(pathElement.getStartIndex()).toString())
          .append(" to ")
          .append(inputBuffer.getPosition(pathElement.getEndIndex() - 1).toString())
          .append(": ");
      int len = pathElement.getEndIndex() - pathElement.getStartIndex();
      if (len > EXCERPT_SIZE) {
        len = EXCERPT_SIZE;
        sb.append("...");
      }
      sb.append('"');
      for (int i = pathElement.getEndIndex() - len; i < pathElement.getEndIndex(); i++) {
        sb.append(TextUtils.escape(inputBuffer.charAt(i)));
      }
      sb.append('"');
    }
    sb.append('\n');
  }

  private static void appendSnippet(StringBuilder sb, InputBuffer inputBuffer, Position position) {
    int startLine = Math.max(position.getLine() - SNIPPET_SIZE, 1);
    int endLine = Math.min(position.getLine() + SNIPPET_SIZE, inputBuffer.getLineCount());
    int padding = Integer.toString(endLine).length();
    String lineNumberFormat = "%1$" + padding + "d: ";
    for (int line = startLine; line <= endLine; line++) {
      sb.append(String.format(lineNumberFormat, line));
      sb.append(TextUtils.trimTrailingLineSeparatorFrom(inputBuffer.extractLine(line)).replace("\t", " ")).append('\n');
      if (line == position.getLine()) {
        for (int i = 1; i < position.getColumn() + padding + 2; i++) {
          sb.append(' ');
        }
        sb.append("^\n");
      }
    }
  }

}
