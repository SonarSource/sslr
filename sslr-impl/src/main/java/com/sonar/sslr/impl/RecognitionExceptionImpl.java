/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl;

import com.sonar.sslr.api.RecognitionException;

public class RecognitionExceptionImpl extends RuntimeException implements RecognitionException {

  private static final long serialVersionUID = 9043689248001323911L;

  private int line;

  private static RecognitionExceptionImpl exception = new RecognitionExceptionImpl();

  private RecognitionExceptionImpl() {
  }

  public RecognitionExceptionImpl(ParsingState parsingState) {
    super(ParsingStackTrace.generateFullStackTrace(parsingState));
    if (parsingState.getOutpostMatcherToken() != null) {
      line = parsingState.getOutpostMatcherToken().getLine();
    }
  }
  
  public RecognitionExceptionImpl(String message, ParsingState parsingState, Throwable e) {
    super(message + "\n" + ParsingStackTrace.generateFullStackTrace(parsingState), e);
    if (parsingState.getOutpostMatcherToken() != null) {
      line = parsingState.getOutpostMatcherToken().getLine();
    }
  }

  public int getLine() {
    return line;
  }

  public static RecognitionExceptionImpl create() {
    return exception;
  }
}
