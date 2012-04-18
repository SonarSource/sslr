/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.devkit;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.Token;

import java.util.Map;

public final class Offsets {

  private final Map<Integer, Integer> lineOffsets = Maps.newHashMap();
  private int endPositionOffset;

  public void computeLineOffsets(String code, int endPositionOffset) {
    this.endPositionOffset = endPositionOffset;
    lineOffsets.clear();

    int currentOffset = 1;

    String[] lines = code.split("(\r)?\n", -1);
    for (int line = 1; line <= lines.length; line++) {
      lineOffsets.put(line, currentOffset);
      currentOffset += lines[line - 1].length() + 1;
    }
  }

  public int getLineFromOffset(int offset) {
    int line;

    for (line = 1; lineOffsets.containsKey(line + 1) && offset >= lineOffsets.get(line + 1); line++) {
    }

    return line;
  }

  public int getColumnFromOffsetAndLine(int offset, int line) {
    return offset - lineOffsets.get(line);
  }

  public int getStartOffset(Token token) {
    return getOffset(token.getLine(), token.getColumn());
  }

  public int getEndOffset(Token token) {
    String[] tokenLines = token.getOriginalValue().split("(\r)?\n", -1);

    int tokenLastLine = token.getLine() + tokenLines.length - 1;
    int tokenLastLineColumn = (tokenLines.length > 1 ? 0 : token.getColumn()) + tokenLines[tokenLines.length - 1].length();

    return getOffset(tokenLastLine, tokenLastLineColumn);
  }

  public int getOffset(int line, int column) {
    return lineOffsets.containsKey(line) ? Math.min(lineOffsets.get(line) + column, endPositionOffset - 1) : endPositionOffset - 1;
  }

}
