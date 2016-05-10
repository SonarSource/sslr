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
package org.sonar.sslr.internal.toolkit;

import com.sonar.sslr.api.Token;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class LineOffsets {

  private static final String NEWLINE_REGEX = "(\r)?\n|\r";

  private final Map<Integer, Integer> lineOffsets = new HashMap<>();
  private final int endOffset;

  public LineOffsets(String code) {
    int currentOffset = 0;

    String[] lines = code.split(NEWLINE_REGEX, -1);
    for (int line = 1; line <= lines.length; line++) {
      lineOffsets.put(line, currentOffset);
      currentOffset += lines[line - 1].length() + 1;
    }

    endOffset = currentOffset - 1;
  }

  public int getStartOffset(Token token) {
    return getOffset(token.getLine(), token.getColumn());
  }

  public int getEndOffset(Token token) {
    String[] tokenLines = token.getOriginalValue().split(NEWLINE_REGEX, -1);

    int tokenLastLine = token.getLine() + tokenLines.length - 1;
    int tokenLastLineColumn = (tokenLines.length > 1 ? 0 : token.getColumn()) + tokenLines[tokenLines.length - 1].length();

    return getOffset(tokenLastLine, tokenLastLineColumn);
  }

  public int getOffset(int line, int column) {
    checkArgument(line >= 1);
    checkArgument(column >= 0);

    if (lineOffsets.containsKey(line)) {
      return Math.min(lineOffsets.get(line) + column, endOffset);
    } else {
      return endOffset;
    }
  }

}
