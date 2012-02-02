/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.channel.CodeReaderConfiguration;

import com.sonar.sslr.api.*;

public final class Lexer {

  private static final int DEFAULT_CODE_BUFFER_CAPACITY = 80000; // The default 8'000 buffer capacity is extended to 80'000 to be able to
                                                                 // consume big comment

  private final Charset charset;
  private final CodeReaderConfiguration configuration;
  private final ChannelDispatcher<Lexer> channelDispatcher;
  private final Preprocessor[] preprocessors;

  private String filename;
  private final List<Trivia> trivia = new LinkedList<Trivia>();
  private List<Token> tokens = new ArrayList<Token>();

  private Lexer(Builder builder) {
    this.charset = builder.charset;
    this.preprocessors = builder.preprocessors.toArray(new Preprocessor[builder.preprocessors.size()]);
    this.configuration = builder.configuration;
    this.channelDispatcher = builder.getChannelDispatcher();
  }

  public List<Token> lex(String sourceCode) {
    filename = null;
    return lex(new StringReader(sourceCode));
  }

  public List<Token> lex(File file) {
    filename = file.getAbsolutePath();
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(new FileInputStream(file), charset);
      return lex(reader);
    } catch (FileNotFoundException e) {
      throw new LexerException("Unable to open file : " + file.getAbsolutePath(), e);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  private List<Token> lex(Reader reader) {
    tokens = new ArrayList<Token>();

    startLexing();
    CodeReader code = new CodeReader(reader, configuration);
    try {
      channelDispatcher.consume(code, this);
      addToken(Token.builder(GenericTokenType.EOF, "EOF").withLine(code.getLinePosition()).withColumn(code.getColumnPosition()).build());

      preprocess();

      endLexing();

      return getTokens();
    } catch (Exception e) {
      throw new LexerException("Unable to lex source code at line : " + code.getLinePosition() + " and column : "
          + code.getColumnPosition() + " in file : " + filename, e);
    }
  }

  private void preprocess() {
    for (Preprocessor preprocessor : preprocessors) {
      preprocess(preprocessor);
    }
  }

  private void preprocess(Preprocessor preprocessor) {
    List<Token> remainingTokens = new LinkedList<Token>();
    remainingTokens.addAll(tokens);
    tokens.clear();

    while ( !remainingTokens.isEmpty()) {
      PreprocessorAction action = preprocessor.process(Collections.unmodifiableList(remainingTokens));
      if (action == null) {
        throw new IllegalStateException("A preprocessor should not return null as a preprocessor action!");
      }

      addTrivia(action.getTriviaToInject().toArray(new Trivia[action.getTriviaToInject().size()]));

      for (int i = 0; i < action.getNumberOfConsumedTokens(); i++) {
        Token removedToken = remainingTokens.remove(0);
        addTrivia(removedToken.getTrivia().toArray(new Trivia[removedToken.getTrivia().size()]));
      }

      for (Token tokenToInject : action.getTokensToInject()) {
        addToken(tokenToInject);
      }

      if (action.getNumberOfConsumedTokens() == 0) {
        Token removedToken = remainingTokens.remove(0);
        addToken(removedToken);
      }
    }
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
  public void endLexing() {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.endLexing(this);
    }
  }

  public void addTrivia(Trivia... trivia) {
    if (trivia.length > 0) {
      this.trivia.addAll(Arrays.asList(trivia));
    }
  }

  public void addToken(Token... tokens) {
    if (tokens.length > 0) {
      tokens[0].addAllTrivia(trivia);
      trivia.clear();
      this.tokens.addAll(Arrays.asList(tokens));
    }
  }

  public List<Token> getTokens() {
    return Collections.unmodifiableList(tokens);
  }

  public String getFilename() {
    return filename;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * @deprecated no one should need this method, it will be deleted.
   * @param tokens
   */
  @Deprecated
  public void setTokens(List<Token> tokens) {
    this.tokens = tokens;
  }

  public static final class Builder {

    private Charset charset = Charset.defaultCharset();
    private final List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
    private final CodeReaderConfiguration configuration = new CodeReaderConfiguration();
    private final List<Channel<Lexer>> channels = new ArrayList<Channel<Lexer>>();
    private boolean failIfNoChannelToConsumeOneCharacter = false;

    private Builder() {
      configuration.setBufferCapacity(DEFAULT_CODE_BUFFER_CAPACITY);
    }

    public Lexer build() {
      return new Lexer(this);
    }

    public Builder withCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public Builder withPreprocessor(Preprocessor preprocessor) {
      preprocessors.add(preprocessor);
      return this;
    }

    public Builder withChannel(Channel<Lexer> channel) {
      channels.add(channel);
      return this;
    }

    public Builder withFailIfNoChannelToConsumeOneCharacter(boolean failIfNoChannelToConsumeOneCharacter) {
      this.failIfNoChannelToConsumeOneCharacter = failIfNoChannelToConsumeOneCharacter;
      return this;
    }

    private ChannelDispatcher<Lexer> getChannelDispatcher() {
      return new ChannelDispatcher<Lexer>((List) channels, failIfNoChannelToConsumeOneCharacter);
    }

  }

}
