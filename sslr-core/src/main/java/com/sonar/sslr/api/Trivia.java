/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class Trivia {

  public enum TriviaKind {
    COMMENT,
    PREPROCESSOR,
    SKIPPED_TEXT
  }

  private final TriviaKind kind;
  private final List<Token> tokens;
  private final PreprocessingDirective preprocessingDirective;

  private Trivia(TriviaKind kind, Token... tokens) {
    this(kind, null, tokens);
  }

  private Trivia(TriviaKind kind, PreprocessingDirective preprocessingDirective, Token... tokens) {
    this.kind = kind;
    this.preprocessingDirective = preprocessingDirective;
    this.tokens = Collections.unmodifiableList(Arrays.asList(tokens));
  }

  public Token getToken() {
    checkState(!tokens.isEmpty(), "the trivia must have at least one associated token to be able to call getToken()");
    return tokens.get(0);
  }

  public List<Token> getTokens() {
    return tokens;
  }

  public boolean isComment() {
    return kind == TriviaKind.COMMENT;
  }

  public boolean isPreprocessor() {
    return kind == TriviaKind.PREPROCESSOR;
  }

  public boolean isSkippedText() {
    return kind == TriviaKind.SKIPPED_TEXT;
  }

  public boolean hasPreprocessingDirective() {
    return preprocessingDirective != null;
  }

  public PreprocessingDirective getPreprocessingDirective() {
    return preprocessingDirective;
  }

  @Override
  public String toString() {
    if (tokens.isEmpty()) {
      return "TRIVIA kind=" + kind;
    } else if (tokens.size() == 1) {
      Token token = tokens.get(0);
      return "TRIVIA kind=" + kind + " line=" + token.getLine() + " type=" + token.getType() + " value=" + token.getOriginalValue();
    } else {
      StringBuilder sb = new StringBuilder();
      for (Token token : tokens) {
        sb.append(token.getOriginalValue());
        sb.append(' ');
      }

      return "TRIVIA kind=" + kind + " value = " + sb.toString();
    }
  }

  public static Trivia createComment(Token commentToken) {
    return new Trivia(TriviaKind.COMMENT, commentToken);
  }

  public static Trivia createSkippedText(List<Token> tokens) {
    checkNotNull(tokens, "tokens cannot be null");

    return createSkippedText(tokens.toArray(new Token[tokens.size()]));
  }

  public static Trivia createSkippedText(Token... tokens) {
    return new Trivia(TriviaKind.SKIPPED_TEXT, tokens);
  }

  public static Trivia createPreprocessingToken(Token preprocessingToken) {
    return new Trivia(TriviaKind.PREPROCESSOR, preprocessingToken);
  }

  public static Trivia createPreprocessingDirective(PreprocessingDirective preprocessingDirective) {
    return new Trivia(TriviaKind.PREPROCESSOR, preprocessingDirective);
  }

  public static Trivia createPreprocessingDirective(AstNode ast, Grammar grammar) {
    return createPreprocessingDirective(PreprocessingDirective.create(ast, grammar));
  }

}
