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
package org.sonar.sslr.parser;

import org.sonar.sslr.internal.matchers.InputBuffer;

import java.util.Objects;

/**
 * Describes an error, which is occurred during parse.
 * Use {@link ParseErrorFormatter} to convert instances of this class to readable format.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 *
 * @since 1.16
 */
public class ParseError {

  private final InputBuffer inputBuffer;
  private final int errorIndex;

  public ParseError(InputBuffer inputBuffer, int errorIndex) {
    this.inputBuffer = Objects.requireNonNull(inputBuffer, "inputBuffer");
    this.errorIndex = errorIndex;
  }

  public InputBuffer getInputBuffer() {
    return inputBuffer;
  }

  public int getErrorIndex() {
    return errorIndex;
  }

}
