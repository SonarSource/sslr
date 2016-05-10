/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonar.sslr.internal.text;

import org.sonar.sslr.text.Text;
import org.sonar.sslr.text.TextCharSequence;

public class TransformedText extends AbstractText {

  private final AbstractText fromText;
  private final AbstractText toText;

  public TransformedText(AbstractText fromText, AbstractText toText) {
    this.fromText = fromText;
    this.toText = toText;
  }

  @Override
  public int length() {
    return toText.length();
  }

  @Override
  public TextCharSequence sequence() {
    return toText.sequence();
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
