/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Trivia {

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
    this.tokens = Arrays.asList(tokens);
    if (this.tokens.isEmpty()) {
      throw new IllegalArgumentException("the trivia must have at least one associated token to be able to call getToken()");
    }
  }

  public Token getToken() {
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
    Objects.requireNonNull(tokens, "tokens cannot be null");

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
