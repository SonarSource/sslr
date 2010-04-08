/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.api;

/**
 * If a parsing error is encountered, an exception which implements this RecognitionException is thrown by the Parser. This
 * RecognitionException allows to get some contextual information about the parsing error like the parsing stack trace.
 */
public interface RecognitionException {

  /**
   * Line where the parsing error has occurred.
   * 
   * @return line
   */
  public int getLine();

  /**
   * The full parsing stack trace. Here is an example :
   * <p>
   * Expected : <language> but was : <lang [WORD]> ('file1': Line 34 / Column 46)
   *       at ParentRule := ((language | implements))
   *       at GrandParentRule := (ParentRule)
   * </p>
   * 
   * @return parsing stack trace.
   */
  public String getMessage();
}
