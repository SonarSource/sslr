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
package org.sonar.sslr.parser;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.sonar.sslr.internal.matchers.InputBuffer;
import org.sonar.sslr.internal.matchers.ParseNode;

import javax.annotation.Nullable;

/**
 * Parsing result.
 *
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 *
 * @since 2.0
 */
public class ParsingResult {

  private final boolean matched;
  private final ParseNode parseTreeRoot;
  private final InputBuffer inputBuffer;
  private final ParseError parseError;

  public ParsingResult(InputBuffer inputBuffer, boolean matched, @Nullable ParseNode parseTreeRoot, @Nullable ParseError parseError) {
    this.inputBuffer = Preconditions.checkNotNull(inputBuffer, "inputBuffer");
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

  @VisibleForTesting
  public ParseNode getParseTreeRoot() {
    return parseTreeRoot;
  }

}
