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

import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextCursor;
import org.sonar.sslr.text.TextLine;

import java.util.Iterator;

public class TextLineCursorImpl implements Iterator<TextLine> {

  private boolean hasNextAlreadyBeenCalledYet;
  private final TextCursor cursor;

  public TextLineCursorImpl(Text text) {
    this.hasNextAlreadyBeenCalledYet = false;
    this.cursor = text.cursor();
  }

  public boolean hasNext() {
    return !hasNextAlreadyBeenCalledYet || !cursor.isEmpty();
  }

  public TextLine next() {
    int lineNumber = !hasNextAlreadyBeenCalledYet ? 1 : cursor.getPosition().getLine();
    int startIndex = cursor.getIndex();
    int endOfLineIndex = cursor.getText().length();
    int endIndex = cursor.getText().length();

    while (!cursor.isEmpty() && lineNumber == cursor.getPosition().getLine()) {
      if (TextUtils.isCrOrLf(cursor, 0)) {
        endOfLineIndex = cursor.getIndex();

        if (TextUtils.isCrLf(cursor, 0)) {
          // Move from the \r to the \n
          cursor.moveForward(1);
        }

        cursor.moveForward(1);
        endIndex = cursor.getIndex();
      } else {
        cursor.moveForward(1);
      }
    }

    hasNextAlreadyBeenCalledYet = true;

    return new TextLineImpl(cursor.getText(), startIndex, endOfLineIndex, endIndex);
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

}
