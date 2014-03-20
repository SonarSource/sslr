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
package org.sonar.sslr.channel;

public abstract class Channel<O> {

  /**
   * Tries to consume the character stream at the current reading cursor position (provided by the {@link org.sonar.sslr.channel.CodeReader}). If
   * the character stream is consumed the method must return true and the OUTPUT object can be fed.
   * 
   * @param code
   *          the handle on the input character stream
   * @param output
   *          the OUTPUT that can be optionally fed by the Channel
   * @return false if the Channel doesn't want to consume the character stream, true otherwise.
   */
  public abstract boolean consume(CodeReader code, O output);
}
