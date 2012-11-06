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
package com.sonar.sslr.api;

import com.sonar.sslr.impl.LexerException;
import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.ExtendedStackTrace;

/**
 * If a parsing error is encountered, an exception which implements this RecognitionException is thrown by the Parser. This
 * RecognitionException allows to get some contextual information about the parsing error like the parsing stack trace.
 */
public class RecognitionException extends RuntimeException {

  private final int line;
  private final boolean isToRetryWithExtendStackTrace;
  private final boolean isFatal;

  public RecognitionException(ParsingState parsingState, boolean isFatal) {
    super(ParsingStackTrace.generateFullStackTrace(parsingState));
    line = parsingState.getOutpostMatcherToken() == null ? 0 : parsingState.getOutpostMatcherToken().getLine();
    isToRetryWithExtendStackTrace = true;
    this.isFatal = isFatal;
  }

  public RecognitionException(String message, ParsingState parsingState, boolean isFatal, Throwable e) {
    super(message + "\n" + ParsingStackTrace.generateFullStackTrace(parsingState), e);
    line = parsingState.getOutpostMatcherToken() == null ? 0 : parsingState.getOutpostMatcherToken().getLine();
    isToRetryWithExtendStackTrace = false;
    this.isFatal = isFatal;
  }

  public RecognitionException(ExtendedStackTrace extendedStackTrace, boolean isFatal) {
    super(extendedStackTrace.toString());
    line = extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getLine();
    isToRetryWithExtendStackTrace = false;
    this.isFatal = isFatal;
  }

  public RecognitionException(LexerException e) {
    super("Lexer error: " + e.getMessage(), e);
    line = 0;
    isToRetryWithExtendStackTrace = false;
    this.isFatal = true;
  }

  /**
   * @since 1.16
   */
  public RecognitionException(int line, String message) {
    super(message);
    this.line = line;
    this.isToRetryWithExtendStackTrace = false;
    this.isFatal = true;
  }

  /**
   * @since 1.16
   */
  public RecognitionException(int line, String message, Throwable cause) {
    super(message, cause);
    this.line = line;
    this.isToRetryWithExtendStackTrace = false;
    this.isFatal = true;
  }

  /**
   * Line where the parsing error has occurred.
   *
   * @return line
   */
  public int getLine() {
    return line;
  }

  /**
   * @return Whether or not it is worth to retry the parsing with the extended stack trace enabled.
   */
  public boolean isToRetryWithExtendStackTrace() {
    return isToRetryWithExtendStackTrace;
  }

  /**
   * @return True if this recognition exception is a fatal one (i.e. not in recovery mode).
   */
  public boolean isFatal() {
    return isFatal;
  }

}
