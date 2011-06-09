/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.io.File;

public class Token {

  private Token previousToken;
  private Token followingToken;
  private TokenType type;
  private String value;
  private int line = 1;
  private int column = 0;
  private File file;
  private boolean generatedCode = false;

  private boolean copyBook = false;
  private int copyBookOriginalLine = -1;
  private String copyBookOriginalFileName = null;

  public Token(TokenType type, String value) {
    this.type = type;
    this.value = value;
  }

  public Token(TokenType type, String value, int line, int column) {
    this(type, value);
    this.line = line;
    this.column = column;
  }

  public Token(TokenType type, String value, int line, int column, File file) {
    this(type, value, line, column);
    setFile(file);
  }

  public TokenType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public File getFile() {
    if (file == null) {
      return new File("Dummy for unit tests");
    }
    return file;
  }

  public void setType(TokenType type) {
    this.type = type;
  }

  public boolean isCopyBook() {
    return copyBook;
  }

  public void setCopyBook(boolean copyBook) {
    this.copyBook = copyBook;
  }

  public boolean isGeneratedCode() {
    return generatedCode;
  }

  public void setGeneratedCode(boolean generatedCode) {
    this.generatedCode = generatedCode;
  }

  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + line;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (column != other.column)
			return false;
		if (line != other.line)
			return false;
		return true;
	}

  @Override
  public String toString() {
    return getType() + ": " + getValue();
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setCopyBookOriginalLine(int copyBookOriginalLine) {
    this.copyBookOriginalLine = copyBookOriginalLine;
  }

  public int getCopyBookOriginalLine() {
    return copyBookOriginalLine;
  }

  public void setCopyBookOriginalFileName(String copyBookOriginalFileName) {
    this.copyBookOriginalFileName = copyBookOriginalFileName;
  }

  public String getCopyBookOriginalFileName() {
    return copyBookOriginalFileName;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Token getPreviousToken() {
    return previousToken;
  }

  public void setPreviousToken(Token previousToken) {
    this.previousToken = previousToken;
  }

  public Token getFollowingToken() {
    return followingToken;
  }

  public void setFollowingToken(Token followingToken) {
    this.followingToken = followingToken;
  }
}
