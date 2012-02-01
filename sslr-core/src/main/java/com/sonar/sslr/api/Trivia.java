/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

public final class Trivia {

  public enum TriviaKind {
    COMMENT,
    PREPROCESSOR,
    SKIPPED_TEXT
  }

  private TriviaKind kind;

  private Token[] tokens = new Token[0];
  private PreprocessingDirective preprocessingDirective;

  private Trivia() {
  }

  public Token getToken() {
    return tokens.length == 0 ? null : tokens[0];
  }

  public Token[] getTokens() {
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
    if (tokens.length == 0) {
      return "TRIVIA kind=" + kind;
    } else if (tokens.length == 1) {
      Token token = tokens[0];
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

  private static Trivia createTrivia(TriviaKind kind, Token... tokens) {
    Trivia trivia = new Trivia();
    trivia.kind = kind;
    trivia.tokens = tokens;
    return trivia;
  }

  public static Trivia createComment(Token commentToken) {
    return createTrivia(TriviaKind.COMMENT, commentToken);
  }

  public static Trivia createSkippedText(Token... tokens) {
    if (tokens.length == 0) {
      throw new IllegalArgumentException("At least one token has to be skipped!");
    }

    return createTrivia(TriviaKind.SKIPPED_TEXT, tokens);
  }

  public static Trivia createPreprocessingToken(Token preprocessingToken) {
    return createTrivia(TriviaKind.PREPROCESSOR, preprocessingToken);
  }

  public static Trivia createPreprocessingDirective(PreprocessingDirective preprocessingDirective) {
    Trivia trivia = new Trivia();
    trivia.kind = TriviaKind.PREPROCESSOR;
    trivia.preprocessingDirective = preprocessingDirective;
    return trivia;
  }

  public static Trivia createPreprocessingDirective(AstNode ast, Grammar grammar) {
    return createPreprocessingDirective(PreprocessingDirective.create(ast, grammar));
  }

}
