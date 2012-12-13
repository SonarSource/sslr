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
import org.sonar.sslr.text.Position;
import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextCursor;

/**
 * @since 1.17
 */
public class TextCursorImpl extends AbstractTextOperations implements TextCursor {

  private final Text text;
  private int index;

  public TextCursorImpl(Text text) {
    this.text = text;
    this.index = 0;
  }

  public int length() {
    return text.length() - index;
  }

  public char charAt(int offset) {
    return text.charAt(index + offset);
  }

  public Text subSequence(int start, int end) {
    return text.subSequence(index + start, index + end);
  }

  public Position getPosition() {
    return text.getPosition(index);
  }

  public Position getOriginalPosition() {
    return text.getOriginalPosition(index);
  }

  public void moveForward(int offset) {
    Preconditions.checkArgument(offset >= 1, "offset(" + offset + ") >= 1");
    Preconditions.checkPositionIndex(offset, length());

    index += offset;
  }

  public int getIndex() {
    return index;
  }

  public Text getText() {
    return text;
  }

}
