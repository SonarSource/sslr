/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2023 SonarSource SA
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
package org.sonar.sslr.internal.matchers;

/**
 * Input text to be parsed.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 1.16
 */
public interface InputBuffer {

  int length();

  char charAt(int index);

  /**
   * Returns content of a line for a given line number.
   * Numbering of lines starts from 1.
   */
  String extractLine(int lineNumber);

  /**
   * Returns number of lines, which is always equal to number of line terminators plus 1.
   */
  int getLineCount();

  Position getPosition(int index);

  class Position {

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
      return this.line == other.line
          && this.column == other.column;
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
