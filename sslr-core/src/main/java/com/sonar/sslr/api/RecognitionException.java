/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
package com.sonar.sslr.api;

import com.sonar.sslr.impl.LexerException;

/**
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 */
public class RecognitionException extends RuntimeException {

  private final int line;

  public RecognitionException(LexerException e) {
    super("Lexer error: " + e.getMessage(), e);
    this.line = 0;
  }

  /**
   * @since 1.16
   */
  public RecognitionException(int line, String message) {
    super(message);
    this.line = line;
  }

  /**
   * @since 1.16
   */
  public RecognitionException(int line, String message, Throwable cause) {
    super(message, cause);
    this.line = line;
  }

  /**
   * Line where the parsing error has occurred.
   *
   * @return line
   */
  public int getLine() {
    return line;
  }

}
