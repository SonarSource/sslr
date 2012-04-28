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

import com.google.common.collect.Lists;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Tokens are string of character like an identifier, a literal, an integer, ... which are produced by the lexer to feed the parser.
 * By definition, comments and preprocessing directives should not be seen by the parser that's why such Trivia, when they exist, are
 * attached to the next token.
 */
public final class Token {

  private final TokenType type;
  private final String value;
  private final String originalValue;
  private final int line;
  private final int column;
  private final URI uri;
  private final boolean generatedCode;
  private final List<Trivia> trivia;
  private final boolean copyBook;
  private final int copyBookOriginalLine;
  private final String copyBookOriginalFileName;

  private Token(Builder builder) {
    this.type = builder.type;
    this.value = builder.value;
    this.originalValue = builder.originalValue;
    this.line = builder.line;
    this.column = builder.column;
    this.uri = builder.uri;
    this.generatedCode = builder.generatedCode;
    this.trivia = Collections.unmodifiableList(builder.trivia);
    this.copyBook = builder.copyBook;
    this.copyBookOriginalLine = builder.copyBookOriginalLine;
    this.copyBookOriginalFileName = builder.copyBookOriginalFileName;
  }

  public TokenType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  /**
   * @return the original value of the token. This method is useful when a language is case-insensitive as in that case all token values are
   *         capitalized.
   */
  public String getOriginalValue() {
    return originalValue;
  }

  /**
   * @return the line of the token in the source code
   */
  public int getLine() {
    return line;
  }

  /**
   * @return the column of the token in the source code
   */
  public int getColumn() {
    return column;
  }

  /**
   * @return the URI this token belongs to
   */
  public URI getURI() {
    return uri;
  }

  public boolean isCopyBook() {
    return copyBook;
  }

  public boolean isGeneratedCode() {
    return generatedCode;
  }

  /**
   * @return true if there is some trivia like some comments or preprocessing directive between this token and the previous one.
   */
  public boolean hasTrivia() {
    return !trivia.isEmpty();
  }

  /**
   * @return the list of trivia located between this token and the previous one
   */
  public List<Trivia> getTrivia() {
    return trivia;
  }

  public int getCopyBookOriginalLine() {
    return copyBookOriginalLine;
  }

  public String getCopyBookOriginalFileName() {
    return copyBookOriginalFileName;
  }

  public boolean isOnSameLineThan(Token other) {
    return other == null ? false : getLine() == other.getLine();
  }

  @Override
  public String toString() {
    return getType() + ": " + getValue();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Token token) {
    return new Builder(token);
  }

  public static final class Builder {

    private TokenType type;
    private String value;
    private String originalValue;
    private URI uri;
    private int line = 0;
    private int column = -1;
    private List<Trivia> trivia = Collections.EMPTY_LIST;
    private boolean generatedCode = false;
    private boolean copyBook = false;
    private int copyBookOriginalLine = -1;
    private String copyBookOriginalFileName = "";

    private Builder() {
    }

    private Builder(Token token) {
      type = token.type;
      value = token.value;
      originalValue = token.originalValue;
      uri = token.uri;
      line = token.line;
      column = token.column;
      trivia = token.trivia;
      generatedCode = token.generatedCode;
      copyBook = token.copyBook;
      copyBookOriginalLine = token.copyBookOriginalLine;
      copyBookOriginalFileName = token.copyBookOriginalFileName;
    }

    public Builder setType(TokenType type) {
      checkNotNull(type, "type cannot be null");

      this.type = type;
      return this;
    }

    public Builder setValueAndOriginalValue(String valueAndOriginalValue) {
      checkNotNull(valueAndOriginalValue, "valueAndOriginalValue cannot be null");

      this.value = valueAndOriginalValue;
      this.originalValue = valueAndOriginalValue;
      return this;
    }

    public Builder setValueAndOriginalValue(String value, String originalValue) {
      checkNotNull(value, "value cannot be null");
      checkNotNull(originalValue, "originalValue cannot be null");

      this.value = value;
      this.originalValue = originalValue;
      return this;
    }

    public Builder setLine(int line) {
      this.line = line;
      return this;
    }

    public Builder setColumn(int column) {
      this.column = column;
      return this;
    }

    public Builder setURI(URI uri) {
      checkNotNull(uri, "uri cannot be null");

      this.uri = uri;
      return this;
    }

    public Builder setGeneratedCode(boolean generatedCode) {
      this.generatedCode = generatedCode;
      return this;
    }

    public Builder setTrivia(List<Trivia> trivia) {
      checkNotNull(trivia, "trivia can't be null");

      this.trivia = Lists.newArrayList(trivia);
      return this;
    }

    public Builder addTrivia(Trivia trivia) {
      checkNotNull(trivia, "trivia can't be null");

      if (this.trivia.isEmpty()) {
        this.trivia = Lists.newArrayList();
      }

      this.trivia.add(trivia);
      return this;
    }

    public Builder setCopyBook(String copyBookOriginalFileName, int copyBookOriginalLine) {
      checkNotNull(copyBookOriginalFileName, "copyBookOriginalFileName cannot be null");

      this.copyBook = true;
      this.copyBookOriginalFileName = copyBookOriginalFileName;
      this.copyBookOriginalLine = copyBookOriginalLine;
      return this;
    }

    public Token build() {
      checkNotNull(type, "type must be set");
      checkNotNull(value, "value must be set");
      checkNotNull(originalValue, "originalValue must be set");
      checkNotNull(uri, "file must be set");
      checkArgument(line >= 1, "line must be greater or equal than 1");
      checkArgument(column >= 0, "column must be greater or equal than 0");

      return new Token(this);
    }
  }

}
