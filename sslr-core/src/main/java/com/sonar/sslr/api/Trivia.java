/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

public final class Trivia {

  public enum TriviaKind {
    COMMENT,
    PREPROCESSOR
  }

  private TriviaKind kind;

  private Token token;
  private PreprocessingDirective preprocessingDirective;

  private Trivia() {
  }

  public Token getToken() {
    return token;
  }

  public boolean isComment() {
    return kind == TriviaKind.COMMENT;
  }

  public boolean isPreprocessor() {
    return kind == TriviaKind.PREPROCESSOR;
  }

  public boolean hasDirective() {
    return preprocessingDirective != null;
  }

  public PreprocessingDirective getPreprocessingDirective() {
    return preprocessingDirective;
  }

  private static Trivia createTrivia(TriviaKind kind, Token token) {
    Trivia trivia = new Trivia();
    trivia.kind = kind;
    trivia.token = token;
    return trivia;
  }

  public static Trivia createCommentToken(Token commentToken) {
    return createTrivia(TriviaKind.COMMENT, commentToken);
  }

  public static Trivia createPreprocessingToken(Token preprocessingDirective) {
    return createTrivia(TriviaKind.PREPROCESSOR, preprocessingDirective);
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
