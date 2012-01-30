/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.channel.*;

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.LexerOutput;
import com.sonar.sslr.api.Preprocessor;

public final class Lexer {

  private Charset charset = Charset.defaultCharset();

  private static final int DEFAULT_CODE_BUFFER_CAPACITY = 80000; // The default 8'000 buffer capacity is extended to 80'000 to be able to
                                                                 // consume big comment

  private CodeReaderConfiguration configuration = new CodeReaderConfiguration();
  private final ChannelDispatcher<LexerOutput> channelDispatcher;
  private Preprocessor[] preprocessors = new Preprocessor[0];

  private Lexer(Builder builder) {
    this.charset = builder.charset;
    this.preprocessors = builder.preprocessors.toArray(new Preprocessor[builder.preprocessors.size()]);
    this.configuration = builder.configuration;
    this.channelDispatcher = builder.getChannelDispatcher();
  }

  public Charset getCharset() {
    return charset;
  }

  public Preprocessor[] getPreprocessors() {
    return preprocessors;
  }

  public LexerOutput lex(String sourceCode) {
    LexerOutput lexerOutput = createLexerOutput();
    lex(new StringReader(sourceCode), lexerOutput);
    return lexerOutput;
  }

  public final LexerOutput lex(File file) {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(new FileInputStream(file), charset);
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

  public final void lex(Reader reader, LexerOutput lexerOutput) {
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
    return new LexerOutput(preprocessors);
  }

  protected ChannelDispatcher<LexerOutput> getChannelDispatcher() {
    return channelDispatcher;
  }

  /**
   * @deprecated use the parser event listeners instead
   */
  @Deprecated
  public void startLexing() {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.startLexing();
    }
  }

  /**
   * @deprecated use the parser event listeners instead
   */
  @Deprecated
  public void endLexing(LexerOutput output) {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.endLexing(output);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Charset charset = Charset.defaultCharset();
    private final List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
    private final CodeReaderConfiguration configuration = new CodeReaderConfiguration();
    private final List<Channel> channels = new ArrayList<Channel>();
    private boolean failIfNoChannelToConsumeOneCharacter = false;

    private Builder() {
      configuration.setBufferCapacity(DEFAULT_CODE_BUFFER_CAPACITY);
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
    public Builder withCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public Builder withPreprocessor(Preprocessor preprocessor) {
      preprocessors.add(preprocessor);
      return this;
    }

    public Builder withChannel(Channel<LexerOutput> channel) {
      channels.add(channel);
      return this;
    }

    public Builder withFailIfNoChannelToConsumeOneCharacter(boolean failIfNoChannelToConsumeOneCharacter) {
      this.failIfNoChannelToConsumeOneCharacter = failIfNoChannelToConsumeOneCharacter;
      return this;
    }

    private ChannelDispatcher<LexerOutput> getChannelDispatcher() {
      return new ChannelDispatcher<LexerOutput>(channels, failIfNoChannelToConsumeOneCharacter);
    }

  }

}
