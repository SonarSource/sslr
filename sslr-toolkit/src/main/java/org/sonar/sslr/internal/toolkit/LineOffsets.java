/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.toolkit;

import com.sonar.sslr.api.Token;

import java.util.HashMap;
import java.util.Map;

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
    if (line < 1) {
      throw new IllegalArgumentException();
    }
    if (column < 0) {
      throw new IllegalArgumentException();
    }

    if (lineOffsets.containsKey(line)) {
      return Math.min(lineOffsets.get(line) + column, endOffset);
    } else {
      return endOffset;
    }
  }

}
