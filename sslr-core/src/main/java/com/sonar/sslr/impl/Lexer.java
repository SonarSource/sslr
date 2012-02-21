/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import static com.google.common.base.Preconditions.*;
import static com.sonar.sslr.api.GenericTokenType.*;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.channel.CodeReaderConfiguration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.Preprocessor;
import com.sonar.sslr.api.PreprocessorAction;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;

public final class Lexer {

  private static final int DEFAULT_CODE_BUFFER_CAPACITY = 80000; // The default 8'000 buffer capacity is extended to 80'000 to be able to
                                                                 // consume big comment

  private final Charset charset;
  private final CodeReaderConfiguration configuration;
  private final ChannelDispatcher<Lexer> channelDispatcher;
  private final Preprocessor[] preprocessors;

  private URI uri;
  private final List<Trivia> trivia = new LinkedList<Trivia>();
  private List<Token> tokens = new ArrayList<Token>();

  private Lexer(Builder builder) {
    this.charset = builder.charset;
    this.preprocessors = builder.preprocessors.toArray(new Preprocessor[builder.preprocessors.size()]);
    this.configuration = builder.configuration;
    this.channelDispatcher = builder.getChannelDispatcher();

    try {
      this.uri = new URI("tests://unittest");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Token> lex(File file) {
    checkNotNull(file, "file cannot be null");
    checkArgument(file.isFile(), "file \"%s\" must be a file", file.getAbsolutePath());

    try {
      return lex(file.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new LexerException("Unable to lex file: " + file.getAbsolutePath(), e);
    }
  }

  public List<Token> lex(URL url) {
    checkNotNull(url, "url cannot be null");

    InputStreamReader reader = null;
    try {
      this.uri = url.toURI();

      reader = new InputStreamReader(url.openStream(), charset);
      return lex(reader);
    } catch (Exception e) {
      throw new LexerException("Unable to lex url: " + getURI(), e);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  /**
   * Do not use this method, it is intended for internal unit testing only
   *
   * @param sourceCode
   * @return
   */
  @VisibleForTesting
  public List<Token> lex(String sourceCode) {
    checkNotNull(sourceCode, "sourceCode cannot be null");

    try {
      return lex(new StringReader(sourceCode));
    } catch (Exception e) {
      throw new LexerException("Unable to lex string source code \"" + sourceCode + "\"", e);
    }
  }

  private List<Token> lex(Reader reader) {
    tokens = Lists.newArrayList();

    initPreprocessors();
    CodeReader code = new CodeReader(reader, configuration);
    try {
      channelDispatcher.consume(code, this);

      addToken(Token.builder()
          .setType(EOF)
          .setValueAndOriginalValue("EOF")
          .setURI(uri)
          .setLine(code.getLinePosition())
          .setColumn(code.getColumnPosition())
          .build());

      preprocess();

      return getTokens();
    } catch (Exception e) {
      throw new LexerException("Unable to lex source code at line : " + code.getLinePosition() + " and column : "
        + code.getColumnPosition() + " in file : " + uri, e);
    }
  }

  private void preprocess() {
    for (Preprocessor preprocessor : preprocessors) {
      preprocess(preprocessor);
    }
  }

  private void preprocess(Preprocessor preprocessor) {
    List<Token> remainingTokens = Collections.unmodifiableList(new ArrayList<Token>(tokens));
    tokens.clear();

    int i = 0;
    while (i < remainingTokens.size()) {
      PreprocessorAction action = preprocessor.process(remainingTokens.subList(i, remainingTokens.size()));
      checkNotNull(action, "A preprocessor cannot return a null PreprocessorAction");

      addTrivia(action.getTriviaToInject());

      for (int j = 0; j < action.getNumberOfConsumedTokens(); j++) {
        Token removedToken = remainingTokens.get(i++);
        addTrivia(removedToken.getTrivia());
      }

      for (Token tokenToInject : action.getTokensToInject()) {
        addToken(tokenToInject);
      }

      if (action.getNumberOfConsumedTokens() == 0) {
        Token removedToken = remainingTokens.get(i++);
        addTrivia(removedToken.getTrivia());
        addToken(removedToken);
      }
    }
  }

  private void initPreprocessors() {
    for (Preprocessor preprocessor : preprocessors) {
      preprocessor.init();
    }
  }

  public void addTrivia(Trivia... trivia) {
    addTrivia(Arrays.asList(trivia));
  }

  public void addTrivia(List<Trivia> trivia) {
    checkNotNull(trivia, "trivia cannot be null");

    this.trivia.addAll(trivia);
  }

  public void addToken(Token... tokens) {
    checkArgument(tokens.length > 0, "at least one token must be given");

    Token firstToken = tokens[0];
    Token firstTokenWithTrivia = Token.builder(firstToken).setTrivia(trivia).build();
    this.tokens.add(firstTokenWithTrivia);
    trivia.clear();
    this.tokens.addAll(Arrays.asList(tokens).subList(1, tokens.length));
  }

  public List<Token> getTokens() {
    return Collections.unmodifiableList(tokens);
  }

  public URI getURI() {
    return uri;
  }

  public static Builder builder() {
    return new Builder();
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
      ChannelDispatcher.Builder builder = ChannelDispatcher.builder()
          .addChannels(channels.toArray(new Channel[channels.size()]));

      if (failIfNoChannelToConsumeOneCharacter) {
        builder.failIfNoChannelToConsumeOneCharacter();
      }

      return builder.build();
    }

  }

}
