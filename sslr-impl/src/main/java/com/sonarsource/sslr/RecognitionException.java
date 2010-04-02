/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr;

public class RecognitionException extends RuntimeException {

  private int line;

  private static RecognitionException exception = new RecognitionException();

  private RecognitionException() {
  }

  public RecognitionException(ParsingState parsingState) {
    super(ParsingStackTrace.generate(parsingState));
    if (parsingState.getOutpostMatcherToken() != null) {
      line = parsingState.getOutpostMatcherToken().getLine();
    }
  }

  public int getLine() {
    return line;
  }

  public static RecognitionException create() {
    return exception;
  }
}
