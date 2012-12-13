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
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextLine;

public class TextLineImpl extends AbstractTextOperations implements TextLine {

  private final Text text;
  private final int startIndex;
  private final int endOfLineIndex;
  private final int endIndex;

  public TextLineImpl(Text text, int startIndex, int endOfLineIndex, int endIndex) {
    this.text = text;
    this.startIndex = startIndex;
    this.endOfLineIndex = endOfLineIndex;
    this.endIndex = endIndex;
  }

  public int length() {
    return endOfLineIndex - startIndex;
  }

  public char charAt(int offset) {
    Preconditions.checkElementIndex(offset, length());
    return text.charAt(startIndex + offset);
  }

  public Text subSequence(int start, int end) {
    Preconditions.checkPositionIndexes(start, end, length());
    return text.subSequence(start + startIndex, end + startIndex);
  }

  public int getLineNumber() {
    return startIndex == 0 ? 1 : text.getPosition(startIndex).getLine();
  }

  public int getIndex() {
    return startIndex;
  }

  public Text getLineTerminator() {
    return text.subSequence(endOfLineIndex, endIndex);
  }

  public Text getText() {
    return text;
  }

}
