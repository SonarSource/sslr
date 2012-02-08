/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
