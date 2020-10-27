/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
package org.sonar.sslr.parser;

import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.InputBuffer.Position;
import org.sonar.sslr.internal.matchers.TextUtils;

import java.util.Objects;

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

  public String format(ParseError parseError) {
    Objects.requireNonNull(parseError);

    InputBuffer inputBuffer = parseError.getInputBuffer();
    Position position = inputBuffer.getPosition(parseError.getErrorIndex());
    StringBuilder sb = new StringBuilder();
    sb.append("Parse error at line ").append(position.getLine())
        .append(" column ").append(position.getColumn())
        .append(":\n\n");
    appendSnippet(sb, inputBuffer, position);
    return sb.toString();
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
