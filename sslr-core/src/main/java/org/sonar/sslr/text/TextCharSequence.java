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
package org.sonar.sslr.text;

/**
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @see Text#sequence()
 * @since 1.17
 * @deprecated in 1.20, use your own text API instead.
 */
@Deprecated
public interface TextCharSequence extends CharSequence {

  @Override
  int length();

  @Override
  char charAt(int index);

  /**
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   */
  @Override
  TextCharSequence subSequence(int start, int end);

  /**
   * @return a string containing the characters in this sequence in the same order as this sequence
   */
  @Override
  String toString();

  Text getText();

  /**
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   */
  Text subText(int start, int end);

  TextLocation getLocation(int index);

}
