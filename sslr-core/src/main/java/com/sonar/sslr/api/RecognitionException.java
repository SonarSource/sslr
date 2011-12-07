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

  public RecognitionException(ParsingState parsingState) {
    super(ParsingStackTrace.generateFullStackTrace(parsingState));
    line = parsingState.getOutpostMatcherToken() == null ? 0 : parsingState.getOutpostMatcherToken().getLine();
    isToRetryWithExtendStackTrace = true;
  }

  public RecognitionException(String message, ParsingState parsingState, Throwable e) {
    super(message + "\n" + ParsingStackTrace.generateFullStackTrace(parsingState), e);
    line = parsingState.getOutpostMatcherToken() == null ? 0 : parsingState.getOutpostMatcherToken().getLine();
    isToRetryWithExtendStackTrace = false;
  }

  public RecognitionException(ExtendedStackTrace extendedStackTrace) {
    super(extendedStackTrace.toString());
    line = extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getLine();
    isToRetryWithExtendStackTrace = false;
  }

  public RecognitionException(LexerException e) {
    super("Lexer error.", e);
    line = 0;
    isToRetryWithExtendStackTrace = false;
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
   * @return Whether or not it is worth to retry the parsing with the extended stack trace enabled
   */
  public boolean isToRetryWithExtendStackTrace() {
    return isToRetryWithExtendStackTrace;
  }

}
