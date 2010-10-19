/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.channel.CodeReaderFilter;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Preprocessor;

public abstract class Lexer {

  private Charset charset = Charset.defaultCharset();

  private Preprocessor[] preprocessors = new Preprocessor[0];

  @SuppressWarnings("unchecked")
  private CodeReaderFilter<LexerOutput>[] codeReaderFilters = new CodeReaderFilter[0];

  public Lexer() {
    this(Charset.defaultCharset());
  }

  public Lexer(Charset defaultCharset) {
    this.charset = defaultCharset;
  }

  public void setPreprocessors(Preprocessor... preprocessors) {
    this.preprocessors = preprocessors;
  }

  protected final Preprocessor[] getPreprocessors() {
    return preprocessors;
  }

  public void setCodeReaderFilters(CodeReaderFilter<LexerOutput>... codeReaderFilters) {
    this.codeReaderFilters = codeReaderFilters;
  }

  protected final CodeReaderFilter<LexerOutput>[] getCodeReaderFilters() {
    return codeReaderFilters;
  }

  public Charset getCharset() {
    return charset;
  }

  public LexerOutput lex(String sourceCode) {
    LexerOutput lexerOutput = createLexerOutput();
    initCodeReaderFilters(lexerOutput);
    lex(new StringReader(sourceCode), lexerOutput);
    return lexerOutput;
  }

  public LexerOutput lex(File file) {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(new FileInputStream(file), getCharset());
      LexerOutput lexerOutput = createLexerOutput();
      initCodeReaderFilters(lexerOutput);
      lexerOutput.setFile(file);
      lex(reader, lexerOutput);
      return lexerOutput;
    } catch (FileNotFoundException e) {
      throw new LexerException("Unable to open file '" + file.getName() + "'", e);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  public void lex(Reader reader, LexerOutput lexerOutput) {
    startLexing();
    initCodeReaderFilters(lexerOutput);
    CodeReader code = new CodeReader(reader, codeReaderFilters);
    try {
      getChannelDispatcher().consume(code, lexerOutput);
      lexerOutput.addTokenAndProcess(GenericTokenType.EOF, "EOF", code.getLinePosition(), code.getColumnPosition());
      endLexing(lexerOutput);
    } catch (Exception e) {
      throw new LexerException("Unable to lex source code at line : " + code.getLinePosition() + " and column : "
          + code.getColumnPosition(), e);
    }
  }

  private void initCodeReaderFilters(LexerOutput lexerOutput) {
    for (CodeReaderFilter<LexerOutput> filter : codeReaderFilters) {
      filter.setOutput(lexerOutput);
    }
  }

  protected LexerOutput createLexerOutput() {
    LexerOutput lexerOutput = new LexerOutput(getPreprocessors());
    return lexerOutput;
  }

  protected abstract ChannelDispatcher<LexerOutput> getChannelDispatcher();

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
