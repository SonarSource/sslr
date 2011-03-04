/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

/**
 * A LeftRecursiveRule must be used in place of a Rule in order to support left recursion.
 * Indeed SSLR embeds a LL(*) parser which doesn't support by default such left recursive rule : 
 * additiveExpr.is(or(and(additiveExpr, "+", INTEGER), INTEGER)
 */
public interface LeftRecursiveRule extends Rule {
  
  /**
   * Supporting left recursive rule requires to manage a state that must be (re)initialized before
   * starting parsing a new source code.
   */
  public void endParsing();
}
