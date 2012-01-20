/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import org.apache.commons.lang.StringUtils;

public final class Trivia {

  public enum TriviaKind {
    COMMENT,
    PREPROCESSOR
  }

  private TriviaKind kind;
  private int line;
  private int column;
  private String value;

  private Token token;
  private PreprocessingDirective preprocessingDirective;

  private Trivia() {
  }

  public String getValue() {
    return value;
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

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public boolean hasDirective() {
    return preprocessingDirective != null;
  }

  public PreprocessingDirective getPreprocessingDirective() {
    return preprocessingDirective;
  }

  @Override
  public String toString() {
    return "TRIVIA kind=" + kind + " line=" + line + " value=" + value;
  }

  private static Trivia createTrivia(TriviaKind kind, Token token) {
    Trivia trivia = new Trivia();
    trivia.kind = kind;
    trivia.line = token.getLine();
    trivia.column = token.getColumn();
    trivia.value = token.getOriginalValue();
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
    trivia.line = preprocessingDirective.getAst().getTokenLine();
    trivia.column = preprocessingDirective.getAst().getToken().getColumn();
    trivia.value = StringUtils.join(preprocessingDirective.getAst().getTokens(), ',');
    trivia.preprocessingDirective = preprocessingDirective;
    return trivia;
  }

  public static Trivia createPreprocessingDirective(AstNode ast, Grammar grammar) {
    return createPreprocessingDirective(PreprocessingDirective.create(ast, grammar));
  }

}
