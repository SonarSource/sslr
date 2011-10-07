/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import com.sonar.sslr.impl.ParsingStackTrace;
import com.sonar.sslr.impl.ParsingState;
import com.sonar.sslr.impl.events.ExtendedStackTrace;

/**
 * If a parsing error is encountered, an exception which implements this RecognitionException is thrown by the Parser. This
 * RecognitionException allows to get some contextual information about the parsing error like the parsing stack trace.
 */
public class RecognitionException extends RuntimeException {

  private int line;

  public RecognitionException(ParsingState parsingState) {
    super(ParsingStackTrace.generateFullStackTrace(parsingState));
    if (parsingState.getOutpostMatcherToken() != null) {
      line = parsingState.getOutpostMatcherToken().getLine();
    }
  }

  public RecognitionException(String message, ParsingState parsingState, Throwable e) {
    super(message + "\n" + ParsingStackTrace.generateFullStackTrace(parsingState), e);
    if (parsingState.getOutpostMatcherToken() != null) {
      line = parsingState.getOutpostMatcherToken().getLine();
    }
  }
  
  public RecognitionException(ExtendedStackTrace extendedStackTrace) {
    super(extendedStackTrace.toString());
    line = extendedStackTrace.longestParsingState.readToken(extendedStackTrace.longestIndex).getLine();
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
