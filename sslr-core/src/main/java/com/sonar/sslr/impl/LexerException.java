/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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
package com.sonar.sslr.impl;

/**
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 */
public class LexerException extends RuntimeException {

  private static final long serialVersionUID = 4901910668771476677L;

  public LexerException(String message, Throwable e) {
    super(message, e);
  }

  public LexerException(String message) {
    super(message);
  }

}
