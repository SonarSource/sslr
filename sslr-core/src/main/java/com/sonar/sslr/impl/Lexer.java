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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.channel.CodeReaderConfiguration;
import org.sonar.channel.CodeReaderFilter;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Preprocessor;

public class Lexer {

  private Charset charset = Charset.defaultCharset();

  private static final int DEFAULT_CODE_BUFFER_CAPACITY = 80000; // The default 8'000 buffer capacity is extended to 80'000 to be able to
                                                                 // consume big comment

  private CodeReaderConfiguration configuration = new CodeReaderConfiguration();
  private ChannelDispatcher<LexerOutput> channelDispatcher;
  private Preprocessor[] preprocessors = new Preprocessor[0];

  private Lexer(LexerBuilder builder) {
    this.charset = builder.charset;
    this.preprocessors = builder.preprocessors.toArray(new Preprocessor[0]);
    this.configuration = builder.configuration;
    this.channelDispatcher = builder.getChannelDispatcher();
  }

  /**
   * @deprecated
   * 
   * @see #builder();
   */
  @Deprecated
  public Lexer() {
    this(Charset.defaultCharset());
  }

  /**
   * @deprecated
   * 
   * @see #builder();
   */
  @Deprecated
  public Lexer(Charset defaultCharset) {
    this.charset = defaultCharset;
    configuration = new CodeReaderConfiguration();
    configuration.setBufferCapacity(DEFAULT_CODE_BUFFER_CAPACITY);
  }

  public void setPreprocessors(Preprocessor... preprocessors) {
    this.preprocessors = preprocessors;
  }

  protected final Preprocessor[] getPreprocessors() {
    return preprocessors;
  }

  protected CodeReaderConfiguration getConfiguration() {
    return configuration;
  }

  protected void setConfiguration(CodeReaderConfiguration configuration) {
    this.configuration = configuration;
  }

  public Charset getCharset() {
    return charset;
  }

  public LexerOutput lex(String sourceCode) {
    LexerOutput lexerOutput = createLexerOutput();
    lex(new StringReader(sourceCode), lexerOutput);
    return lexerOutput;
  }

  public LexerOutput lex(File file) {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(new FileInputStream(file), getCharset());
      LexerOutput lexerOutput = createLexerOutput();
      lexerOutput.setFile(file);
      lex(reader, lexerOutput);
      return lexerOutput;
    } catch (FileNotFoundException e) {
      throw new LexerException("Unable to open file : " + file.getAbsolutePath(), e);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  public void lex(Reader reader, LexerOutput lexerOutput) {
    initCodeReaderFilters(lexerOutput);
    startLexing();
    CodeReader code = new CodeReader(reader, configuration);
    try {
      getChannelDispatcher().consume(code, lexerOutput);
      lexerOutput.addTokenAndProcess(GenericTokenType.EOF, "EOF", code.getLinePosition(), code.getColumnPosition());
      endLexing(lexerOutput);
    } catch (Exception e) {
      throw new LexerException("Unable to lex source code at line : " + code.getLinePosition() + " and column : "
          + code.getColumnPosition() + " in file : " + lexerOutput.getFileAbsolutePath(), e);
    }
  }

  private void initCodeReaderFilters(LexerOutput lexerOutput) {
    for (CodeReaderFilter<LexerOutput> filter : configuration.getCodeReaderFilters()) {
      filter.setOutput(lexerOutput);
    }
  }

  protected LexerOutput createLexerOutput() {
    LexerOutput lexerOutput = new LexerOutput(getPreprocessors());
    return lexerOutput;
  }

  protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
    return channelDispatcher;
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

  public static LexerBuilder builder() {
    return new LexerBuilder();
  }

  public static final class LexerBuilder {

    private Charset charset = Charset.defaultCharset();
    private List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
    private CodeReaderConfiguration configuration = new CodeReaderConfiguration();
    private List<Channel> channels = new ArrayList<Channel>();
    private boolean failIfNoChannelToConsumeOneCharacter = false;

    private LexerBuilder() {

    }

    public Lexer build() {
      return new Lexer(this);
    }

    /**
     * Define the charset to be used in order to read the source code.
     * 
     * @param charset
     * @return this LexerBuilder
     */
    public LexerBuilder withCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public LexerBuilder withPreprocessor(Preprocessor preprocessor) {
      preprocessors.add(preprocessor);
      return this;
    }

    public LexerBuilder withCodeReaderConfiguration(CodeReaderConfiguration conf) {
      this.configuration = conf;
      return this;
    }

    public LexerBuilder withChannel(Channel<LexerOutput> channel) {
      channels.add(channel);
      return this;
    }

    public LexerBuilder withFailIfNoChannelToConsumeOneCharacter(boolean failIfNoChannelToConsumeOneCharacter) {
      this.failIfNoChannelToConsumeOneCharacter = failIfNoChannelToConsumeOneCharacter;
      return this;
    }

    private ChannelDispatcher<LexerOutput> getChannelDispatcher() {
      return new ChannelDispatcher<LexerOutput>(channels, failIfNoChannelToConsumeOneCharacter);
    }
 
  }
  
}
