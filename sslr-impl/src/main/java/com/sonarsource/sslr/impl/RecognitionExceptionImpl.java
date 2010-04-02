/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl;

import com.sonarsource.sslr.api.RecognitionException;

public class RecognitionExceptionImpl extends RuntimeException implements RecognitionException {

  private int line;

  private static RecognitionExceptionImpl exception = new RecognitionExceptionImpl();

  private RecognitionExceptionImpl() {
  }

  public RecognitionExceptionImpl(ParsingState parsingState) {
    super(ParsingStackTrace.generate(parsingState));
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
