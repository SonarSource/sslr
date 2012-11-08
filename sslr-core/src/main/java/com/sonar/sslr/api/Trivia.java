/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.api;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
    this.tokens = ImmutableList.of(tokens);
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
