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

import com.google.common.base.Preconditions;
import org.sonar.sslr.text.TextLocation;

import javax.annotation.Nullable;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

public class FileText extends PlainText {

  private final File file;
  private final URI uri;

  /**
   * Indices of lines.
   * Number of elements equal to number of line terminators.
   */
  private final int[] lines;

  public FileText(@Nullable File file, char[] chars) {
    super(chars);
    this.file = file;
    this.uri = file == null ? null : file.toURI();
    this.lines = TextUtils.computeLines(chars);
  }

  public TextLocation getLocation(int index) {
    Preconditions.checkPositionIndex(index, length());
    int line = getLineNumber(index);
    int column = index - getLineStart(line) + 1;
    return new TextLocation(file, uri, line, column);
  }

  private int getLineNumber(int index) {
    int i = Arrays.binarySearch(lines, index);
    return i >= 0 ? i + 2 : -i;
  }

  private int getLineStart(int line) {
    return line == 1 ? 0 : lines[line - 2];
  }

}
