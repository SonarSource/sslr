/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

/**
 * A Preprocessor can be used to modify the content of the LexerOuput before launching the parser.
 * 
 * A proprocessor can be used for instance to replace a token or a list of tokens by another token or list of tokens.
 */
public abstract class Preprocessor {

  /**
   * This method is called just before adding a new Token to the LexerOutput. The Preprocessor is able to intercept this token and do what
   * ever it wants with it.
   * 
   * @param token
   *          the token to preprocess
   * @param output
   *          the lexer output that can be used to inject any tokens.
   * @return false if that token hasn't been pre-processed and true otherwise. If the method returns true, no other preprocessing operations
   *         will be done on this token and this token won't be added to the LexerOutput
   */
  public abstract boolean process(Token token, LexerOutput output);

  /**
   * Method calls after having lexed a source code. Some additional operations can be done by the Preprocessor on the LexerOuput if
   * required.
   */
  public void endLexing(LexerOutput output) {
  }

  /**
   * Method calls before starting lexing the source code. This method can be overridden to initialize a state for instance.
   */
  public void startLexing() {
  }
}
