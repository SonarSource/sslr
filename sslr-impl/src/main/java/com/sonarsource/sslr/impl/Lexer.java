/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.impl;

import java.io.File;
import java.nio.charset.Charset;

public abstract class Lexer { //TODO this class's name should be Lexer

  private Charset charset = Charset.defaultCharset();

  private Preprocessor[] preprocessors = new Preprocessor[0];

  public Lexer() {
  }

  public void setPreprocessors(Preprocessor... preprocessors) {
    this.preprocessors = preprocessors;
  }

  protected final Preprocessor[] getPreprocessors() {
    return preprocessors;
  }

  public abstract LexerOutput lex(File file);

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  public Charset getCharset() {
    return charset;
  }

  public void startLexing() {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.startLexing();
    }
  }

  public void endLexing(LexerOutput output) {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.endLexing(output);
    }
  }
}
