/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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

/**
 * Base class for all implementations of {@link Text}.
 */
public abstract class AbstractText implements Text {

  protected abstract int getTransformationDepth();

  public char[] toChars() {
    char[] chars = new char[length()];
    toCharArray(0, chars, 0, chars.length);
    return chars;
  }

  public abstract void toCharArray(int srcPos, char[] dest, int destPos, int length);

  @Override
  public String toString() {
    return new String(toChars());
  }

}
