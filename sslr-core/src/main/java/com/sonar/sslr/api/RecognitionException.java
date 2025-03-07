/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
