/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

/**
 * A Grammar must be implemented to define the syntactic rules of a language.
 * 
 * @see Rule
 * @see <a href="http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form">Backus–Naur Form</a>
 */
public interface Grammar {

  /**
   * Each Grammar has always an entry point whose name is usually by convention the "Computation Unit".
   * 
   * @return the entry point of this Grammar
   */
  Rule getRootRule();
}
