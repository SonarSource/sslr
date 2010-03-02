/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

public class CodeReader {

  private final Reader code;
  private int lastChar;
  private int linePosition;
  private int columnPosition;
  private int BUFFER_CAPACITY = 8000;
  private char[] buffer = new char[BUFFER_CAPACITY];
  private int buffer_position = 0;
  private int buffer_size = 0;

  public CodeReader(Reader code) {
    this.code = code;
    lastChar = -1;
    linePosition = 1;
    columnPosition = 0;
  }

  public CodeReader(String code) {
    this(new StringReader(code));
  }

  public final int read() {
    if (buffer_position == buffer_size) {
      fillBuffer();
    }
    if (buffer_size == 0) {
      return -1;
    }
    lastChar = buffer[buffer_position++];
    if (lastChar == '\n') {
      linePosition++;
      columnPosition = 0;
    } else {
      columnPosition++;
    }
    return lastChar;
  }

  private void fillBuffer() {
    try {
      int offset = buffer_size - buffer_position;
      if (offset != 0) {
        System.arraycopy(buffer, buffer_position, buffer, 0, buffer_size - buffer_position);
      }
      buffer_position = 0;
      int numberOfChars = code.read(buffer, offset, BUFFER_CAPACITY - offset);
      if (numberOfChars == -1) {
        numberOfChars = 0;
      }
      buffer_size = numberOfChars + offset;
    } catch (IOException e) {
      throw new LexerException(e.getMessage());
    }
  }

  public final int lastChar() {
    return lastChar;
  }

  public final int peek() {
    if (buffer_position >= buffer_size - 1) {
      fillBuffer();
    }
    if (buffer_size == 0) {
      return -1;
    }
    return buffer[buffer_position];
  }

  public final void pop(Appendable appendable) {
    try {
      appendable.append((char) read());
    } catch (IOException e) {
      throw new LexerException(e.getMessage());
    }
  }

  public void close() {
    IOUtils.closeQuietly(code);
  }

  public char[] peek(int index) {
    char[] result = new char[index];

    if (buffer_position >= buffer_size - index) {
      fillBuffer();
    }

    System.arraycopy(buffer, buffer_position, result, 0, Math.min(index, buffer_size - buffer_position));
    return result;
  }

  public void popTo(EndMatcher matcher, Appendable appendable) {
    try {
      do {
        appendable.append((char) read());
      } while (!matcher.match(peek(1)));
    } catch (IOException e) {
      throw new LexerException(e.getMessage());
    }
  }

  public int getLinePosition() {
    return linePosition;
  }

  public int getColumnPosition() {
    return columnPosition;
  }

  /**
   * For unit tests purpose
   */
  public void setColumnPosition(int cp) {
    this.columnPosition = cp;
  }

  /**
   * For unit tests purpose
   */
  public void setLinePosition(int lp) {
    this.linePosition = lp;
  }
}
