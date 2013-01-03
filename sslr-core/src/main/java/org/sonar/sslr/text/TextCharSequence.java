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
package org.sonar.sslr.text;

/**
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @see Text#sequence()
 * @since 1.17
 */
public interface TextCharSequence extends CharSequence {

  int length();

  char charAt(int index);

  /**
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   */
  TextCharSequence subSequence(int start, int end);

  /**
   * @return a string containing the characters in this sequence in the same order as this sequence
   */
  String toString();

  Text getText();

  /**
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   */
  Text subText(int start, int end);

  TextLocation getLocation(int index);

}
