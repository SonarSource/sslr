/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl;

import com.sonar.sslr.api.Preprocessor;
import com.sonar.sslr.api.PreprocessorAction;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.ChannelDispatcher;
import org.sonar.sslr.channel.CodeReader;
import org.sonar.sslr.channel.CodeReaderConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.sonar.sslr.api.GenericTokenType.EOF;

public class Lexer {

  private final Charset charset;
  private final CodeReaderConfiguration configuration;
  private final ChannelDispatcher<Lexer> channelDispatcher;
  private final Preprocessor[] preprocessors;

  private URI uri;
  private final List<Trivia> trivia = new LinkedList<>();
  private List<Token> tokens = new ArrayList<>();

  private Lexer(Builder builder) {
    this.charset = builder.charset;
    this.preprocessors = builder.preprocessors.toArray(new Preprocessor[builder.preprocessors.size()]);
    this.configuration = builder.configuration;
    this.channelDispatcher = builder.getChannelDispatcher();

    try {
      this.uri = new URI("tests://unittest");
    } catch (URISyntaxException e) {
      // Can't happen
      throw new IllegalStateException(e);
    }
  }

  public List<Token> lex(File file) {
    Objects.requireNonNull(file, "file cannot be null");
    if (!file.isFile()) {
      throw new IllegalArgumentException("file \"" + file.getAbsolutePath() + "\" must be a file");
    }

    try {
      return lex(file.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new LexerException("Unable to lex file: " + file.getAbsolutePath(), e);
    }
  }

  public List<Token> lex(URL url) {
    Objects.requireNonNull(url, "url cannot be null");

    try (InputStreamReader reader = new InputStreamReader(url.openStream(), charset)) {
      this.uri = url.toURI();
      return lex(reader);

    } catch (Exception e) {
      throw new LexerException("Unable to lex url: " + getURI(), e);
    }
  }

  /**
   * Do not use this method, it is intended for internal unit testing only
   *
   * @param sourceCode
   * @return
   */
  // @VisibleForTesting
  public List<Token> lex(String sourceCode) {
    Objects.requireNonNull(sourceCode, "sourceCode cannot be null");

    try {
      return lex(new StringReader(sourceCode));
    } catch (Exception e) {
      throw new LexerException("Unable to lex string source code \"" + sourceCode + "\"", e);
    }
  }

  private List<Token> lex(Reader reader) {
    tokens = new ArrayList<>();

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
    List<Token> remainingTokens = Collections.unmodifiableList(new ArrayList<>(tokens));
    tokens.clear();

    int i = 0;
    while (i < remainingTokens.size()) {
      PreprocessorAction action = preprocessor.process(remainingTokens.subList(i, remainingTokens.size()));
      Objects.requireNonNull(action, "A preprocessor cannot return a null PreprocessorAction");

      addTrivia(action.getTriviaToInject());

      for (int j = 0; j < action.getNumberOfConsumedTokens(); j++) {
        Token removedToken = remainingTokens.get(i);
        i++;
        addTrivia(removedToken.getTrivia());
      }

      for (Token tokenToInject : action.getTokensToInject()) {
        addToken(tokenToInject);
      }

      if (action.getNumberOfConsumedTokens() == 0) {
        Token removedToken = remainingTokens.get(i);
        i++;
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
    Objects.requireNonNull(trivia, "trivia cannot be null");

    this.trivia.addAll(trivia);
  }

  public void addToken(Token... tokens) {
    if (tokens.length <= 0) {
      throw new IllegalArgumentException("at least one token must be given");
    }

    Token firstToken = tokens[0];
    Token firstTokenWithTrivia;

    // Performance optimization: no need to rebuild token, if there is no trivia
    if (trivia.isEmpty() && !firstToken.hasTrivia()) {
      firstTokenWithTrivia = firstToken;
    } else {
      firstTokenWithTrivia = Token.builder(firstToken).setTrivia(trivia).build();
      trivia.clear();
    }

    this.tokens.add(firstTokenWithTrivia);
    if (tokens.length > 1) {
      this.tokens.addAll(Arrays.asList(tokens).subList(1, tokens.length));
    }
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
    private final List<Preprocessor> preprocessors = new ArrayList<>();
    private final CodeReaderConfiguration configuration = new CodeReaderConfiguration();
    private final List<Channel<Lexer>> channels = new ArrayList<>();
    private boolean failIfNoChannelToConsumeOneCharacter = false;

    private Builder() {
    }

    public Lexer build() {
      return new Lexer(this);
    }

    public Builder withCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    /**
     * @deprecated in 1.20 - use your own preprocessor instead
     */
    @Deprecated
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
