/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import org.apache.commons.lang.StringUtils;

public class Trivia {

  public enum TriviaKind {
    COMMENT,
    PREPROCESSOR
  }

  private final TriviaKind kind;
  private final int line;
  private final int column;
  private final int length;
  private final String value;
  private final AstNode structure;
  private final Grammar structureGrammar;

  private Trivia(TriviaKind kind, int line, int column, int length, String value) {
    this(kind, line, column, length, value, null, null);
  }

  private Trivia(TriviaKind kind, int line, int column, int length, String value, AstNode structure, Grammar structureGrammar) {
    this.kind = kind;
    this.line = line;
    this.column = column;
    this.length = length;
    this.value = value;
    this.structure = structure;
    this.structureGrammar = structureGrammar;
  }

  public String getValue() {
    return value;
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

  public int getLength() {
    return length;
  }

  public boolean hasStructure() {
    return structure != null;
  }

  public AstNode getStructure() {
    return structure;
  }

  public Grammar getStructureGrammar() {
    return structureGrammar;
  }

  @Override
  public String toString() {
    return "TRIVIA kind=" + kind + " line=" + line + " value=" + value;
  }

  public static Trivia createCommentTrivia(Token commentToken) {
    return new Trivia(TriviaKind.COMMENT, commentToken.getLine(), commentToken.getColumn(), commentToken.getOriginalValue().length(),
        commentToken.getOriginalValue());
  }

  public static Trivia createPreprocessorTrivia(Token preprocessorToken) {
    return new Trivia(TriviaKind.PREPROCESSOR, preprocessorToken.getLine(), preprocessorToken.getColumn(), preprocessorToken
        .getOriginalValue().length(), preprocessorToken.getOriginalValue());
  }

  public static Trivia createPreprocessorTrivia(AstNode structure, Grammar structureGrammar) {
    String value = StringUtils.join(structure.getTokens(), ',');
    return new Trivia(TriviaKind.PREPROCESSOR, structure.getToken().getLine(), structure.getToken().getColumn(), value.length(), value,
        structure, structureGrammar);
  }

}
