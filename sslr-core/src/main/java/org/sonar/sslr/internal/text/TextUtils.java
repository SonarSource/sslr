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
package org.sonar.sslr.internal.text;

import org.sonar.sslr.text.Position;

import java.io.File;

public final class TextUtils {

  private TextUtils()
  {
  }

  // Array of characters

  public static boolean isCrLf(char[] buffer, int index) {
    if (index + 1 >= buffer.length) {
      return false;
    }

    return isCr(buffer, index) && isLf(buffer, index + 1);
  }

  public static boolean isCrOrLf(char[] buffer, int index) {
    return isCr(buffer, index) || isLf(buffer, index);
  }

  public static boolean isCr(char[] buffer, int index) {
    return buffer[index] == '\r';
  }

  public static boolean isLf(char[] buffer, int index) {
    return buffer[index] == '\n';
  }

  // CharSequence

  public static boolean isCrLf(CharSequence buffer, int index) {
    if (index + 1 >= buffer.length()) {
      return false;
    }

    return isCr(buffer, index) && isLf(buffer, index + 1);
  }

  public static boolean isCrOrLf(CharSequence buffer, int index) {
    return isCr(buffer, index) || isLf(buffer, index);
  }

  public static boolean isCr(CharSequence buffer, int index) {
    return buffer.charAt(index) == '\r';
  }

  public static boolean isLf(CharSequence buffer, int index) {
    return buffer.charAt(index) == '\n';
  }

  // Positions

  public static Position[] getPositions(char[] buffer) {
    int currentLine = 1;
    int currentColumn = 1;

    Position[] result = new Position[buffer.length];
    for (int i = 0; i < buffer.length; i++) {
      result[i] = new Position(currentLine, currentColumn);

      if (TextUtils.isCrOrLf(buffer, i)) {
        if (TextUtils.isCrLf(buffer, i)) {
          // Move from the \r to the \n
          i++;
          result[i] = new Position(currentLine, currentColumn + 1);
        }

        currentLine++;
        currentColumn = 1;
      } else {
        currentColumn++;
      }
    }

    return result;
  }

  public static Position[] getPositionsWithFile(Position[] positions, File originalFile) {
    Position[] result = new Position[positions.length];

    for (int i = 0; i < positions.length; i++) {
      Position position = positions[i];
      result[i] = new Position(originalFile, position.getLine(), position.getColumn());
    }

    return result;
  }

}
