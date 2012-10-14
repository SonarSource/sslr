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
package org.sonar.sslr.internal.matchers;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class InputBuffer {

  private static final char CR = '\r';
  private static final char LF = '\n';

  private final char[] buffer;

  /**
   * Indices of lines in buffer.
   */
  private final int[] lines;

  public InputBuffer(char[] buffer) {
    this.buffer = buffer;

    List<Integer> newlines = Lists.newArrayList();
    int i = 0;
    int column = 0;
    while (i < buffer.length) {
      // was taken from sonar-channel CodeBuffer:
      if (buffer[i] == LF || (buffer[i] == CR && (i + 1 < buffer.length) && buffer[i + 1] != LF)) {
        column = 0;
      } else {
        column++;
      }
      if (column == 1) {
        newlines.add(i);
      }
      i++;
    }
    newlines.add(i);
    this.lines = new int[newlines.size()];
    for (i = 0; i < newlines.size(); i++) {
      this.lines[i] = newlines.get(i);
    }
  }

  public String extractLine(int lineNumber) {
    int start = lines[lineNumber - 1];
    int end = lines[lineNumber];
    return new String(buffer, start, end - start);
  }

  public int getLineCount() {
    return lines.length - 1;
  }

  private int getLineNumber(int index) {
    int i = Arrays.binarySearch(lines, index);
    return i >= 0 ? i + 1 : -(i + 1);
  }

  public Position getPosition(int index) {
    int line = getLineNumber(index);
    int column = index - lines[line - 1] + 1;
    return new Position(line, column);
  }

  public static class Position {

    private final int line;
    private final int column;

    public Position(int line, int column) {
      this.line = line;
      this.column = column;
    }

    public int getLine() {
      return line;
    }

    public int getColumn() {
      return column;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof Position)) {
        return false;
      }
      Position other = (Position) obj;
      return this.column == other.column
          && this.line == other.line;
    }

    @Override
    public int hashCode() {
      return 31 * line + column;
    }

    @Override
    public String toString() {
      return "(" + line + ", " + column + ")";
    }

  }

}
