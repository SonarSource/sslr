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

public class TransformedText extends AbstractText {

  private final AbstractText fromText;
  private final AbstractText toText;

  public TransformedText(AbstractText fromText, AbstractText toText) {
    this.fromText = fromText;
    this.toText = toText;
  }

  public int length() {
    return toText.length();
  }

  public TextCursor cursor() {
    return toText.cursor();
  }

  @Override
  protected int getTransformationDepth() {
    return fromText.getTransformationDepth() + 1;
  }

  protected Text getTransformedText() {
    return fromText;
  }

  @Override
  public void toCharArray(int srcPos, char[] dest, int destPos, int length) {
    toText.toCharArray(srcPos, dest, destPos, length);
  }

}
