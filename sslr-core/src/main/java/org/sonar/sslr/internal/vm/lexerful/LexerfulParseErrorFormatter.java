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
package org.sonar.sslr.internal.vm.lexerful;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.matchers.MatcherPathElement;
import org.sonar.sslr.internal.matchers.TextUtils;
import org.sonar.sslr.internal.vm.ErrorTreeNode;

import java.util.List;

public class LexerfulParseErrorFormatter {

  /**
   * Number of tokens in snippet before and after token with error.
   */
  private static final int SNIPPET_SIZE = 30;

  private static final int EXCERPT_SIZE = 10;

  public String format(List<Token> tokens, int errorIndex, List<List<MatcherPathElement>> failedPaths) {
    StringBuilder sb = new StringBuilder();
    Token errorToken = tokens.get(errorIndex);
    sb.append("Parse error at line ").append(errorToken.getLine())
        .append(" column ").append(errorToken.getColumn());
    sb.append(" failed to match");
    if (failedPaths.size() > 1) {
      sb.append(" all of");
    }
    sb.append(':');
    for (List<MatcherPathElement> failedPath : failedPaths) {
      Matcher failedMatcher = Iterables.getLast(failedPath).getMatcher();
      sb.append(' ').append(((RuleDefinition) failedMatcher).getName());
    }
    sb.append('\n').append('\n');
    appendSnippet(sb, tokens, errorIndex);
    sb.append('\n');
    sb.append("Failed at rules:\n");
    ErrorTreeNode tree = ErrorTreeNode.buildTree(failedPaths);
    appendTree(sb, tokens, tree);
    return sb.toString();
  }

  private void appendTree(StringBuilder sb, List<Token> tokens, ErrorTreeNode node) {
    List<ErrorTreeNode> nodes = Lists.newArrayList();
    while (node.children.size() == 1) {
      nodes.add(node);
      node = node.children.get(0);
    }
    appendTree(sb, tokens, node, "", true);
    for (int i = nodes.size() - 1; i >= 0; i--) {
      appendPathElement(sb, tokens, nodes.get(i).pathElement);
    }
  }

  private void appendTree(StringBuilder sb, List<Token> tokens, ErrorTreeNode node, String prefix, boolean isTail) {
    boolean tail = true;
    for (int i = 0; i < node.children.size(); i++) {
      appendTree(sb, tokens, node.children.get(i), prefix + (isTail ? "  " : "| "), tail);
      tail = false;
    }
    sb.append(prefix + (isTail ? "/-" : "+-"));
    appendPathElement(sb, tokens, node.pathElement);
  }

  private static void appendPathElement(StringBuilder sb, List<Token> tokens, MatcherPathElement pathElement) {
    sb.append(((RuleDefinition) pathElement.getMatcher()).getName());
    if (pathElement.getStartIndex() != pathElement.getEndIndex()) {
      sb.append(" consumed from ")
          .append(formatTokenPosition(tokens.get(pathElement.getStartIndex())))
          .append(" to ")
          .append(formatTokenPosition(tokens.get(pathElement.getEndIndex())))
          .append(": ");
      int len = pathElement.getEndIndex() - pathElement.getStartIndex();
      if (len > EXCERPT_SIZE) {
        len = EXCERPT_SIZE;
        sb.append("...");
      }
      for (int i = pathElement.getEndIndex() - len; i < pathElement.getEndIndex(); i++) {
        sb.append(' ');
        appendEscapedToken(sb, tokens.get(i));
      }
    }
    sb.append('\n');
  }

  private static void appendEscapedToken(StringBuilder sb, Token token) {
    String value = token.getOriginalValue();
    for (int i = 0; i < value.length(); i++) {
      sb.append(TextUtils.escape(value.charAt(i)));
    }
  }

  private static String formatTokenPosition(Token token) {
    return "(" + token.getLine() + ", " + token.getColumn() + ")";
  }

  @VisibleForTesting
  static void appendSnippet(StringBuilder sb, List<Token> tokens, int errorIndex) {
    int startToken = Math.max(errorIndex - SNIPPET_SIZE, 0);
    int endToken = Math.min(errorIndex + SNIPPET_SIZE, tokens.size());
    int errorLine = tokens.get(errorIndex).getLine();
    tokens = tokens.subList(startToken, endToken);

    int line = tokens.get(0).getLine();
    int column = tokens.get(0).getColumn();
    sb.append(formatLineNumber(line, errorLine));
    for (Token token : tokens) {
      while (line < token.getLine()) {
        line++;
        column = 0;
        sb.append('\n').append(formatLineNumber(line, errorLine));
      }
      while (column < token.getColumn()) {
        sb.append(' ');
        column++;
      }
      String[] tokenLines = token.getOriginalValue().split("(\r)?\n|\r", -1);
      sb.append(tokenLines[0]);
      column += tokenLines[0].length();
      for (int j = 1; j < tokenLines.length; j++) {
        line++;
        sb.append('\n').append(formatLineNumber(line, errorLine)).append(tokenLines[j]);
        column = tokenLines[j].length();
      }
    }
    sb.append('\n');
  }

  private static String formatLineNumber(int line, int errorLine) {
    return line == errorLine
        ? String.format("%1$5s  ", "-->")
        : String.format("%1$5d: ", line);
  }

}
