/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
import org.sonar.sslr.internal.matchers.ParseNode;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Parsing result.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
 *
 * @since 1.16
 */
public class ParsingResult {

  private final boolean matched;
  private final ParseNode parseTreeRoot;
  private final InputBuffer inputBuffer;
  private final ParseError parseError;

  public ParsingResult(InputBuffer inputBuffer, boolean matched, @Nullable ParseNode parseTreeRoot, @Nullable ParseError parseError) {
    this.inputBuffer = Objects.requireNonNull(inputBuffer, "inputBuffer");
    this.matched = matched;
    this.parseTreeRoot = parseTreeRoot;
    this.parseError = parseError;
  }

  public InputBuffer getInputBuffer() {
    return inputBuffer;
  }

  public boolean isMatched() {
    return matched;
  }

  public ParseError getParseError() {
    return parseError;
  }

  // @VisibleForTesting
  public ParseNode getParseTreeRoot() {
    return parseTreeRoot;
  }

}
