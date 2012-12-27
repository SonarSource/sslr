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

import com.google.common.collect.Lists;
import org.sonar.sslr.text.Texts;

import java.util.List;

public final class TextUtils {

  private TextUtils() {
  }

  private static final int[] EMPTY_INT_ARRAY = new int[0];

  public static int[] computeLines(char[] chars) {
    List<Integer> newlines = Lists.newArrayList();
    int i = 0;
    while (i < chars.length) {
      if (isEndOfLine(chars, i)) {
        newlines.add(i + 1);
      }
      i++;
    }
    if (newlines.isEmpty()) {
      return EMPTY_INT_ARRAY;
    }
    int[] lines = new int[newlines.size()];
    for (i = 0; i < newlines.size(); i++) {
      lines[i] = newlines.get(i);
    }
    return lines;
  }

  /**
   * A line is considered to be terminated by any one of
   * a line feed ({@code '\n'}), a carriage return ({@code '\r'}),
   * or a carriage return followed immediately by a line feed ({@code "\r\n"}).
   */
  private static boolean isEndOfLine(char[] buffer, int i) {
    return buffer[i] == Texts.LF ||
      buffer[i] == Texts.CR && (i + 1 < buffer.length && buffer[i + 1] != Texts.LF || i + 1 == buffer.length);
  }

}
