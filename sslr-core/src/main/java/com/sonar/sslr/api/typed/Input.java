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
package com.sonar.sslr.api.typed;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 1.21
 */
public class Input {

  private static final URI FAKE_URI = new File("tests://unittests").toURI();

  private final char[] inputChars;
  private final URI uri;
  private final int[] newLineIndexes;

  public Input(char[] input) {
    this(input, FAKE_URI);
  }

  public Input(char[] input, URI uri) {
    this.inputChars = input;
    this.uri = uri;

    List<Integer> newLineIndexesBuilder = new ArrayList<>();
    for (int i = 0; i < input.length; i++) {
      if (isNewLine(input, i)) {
        newLineIndexesBuilder.add(i + 1);
      }
    }
    this.newLineIndexes = new int[newLineIndexesBuilder.size()];
    for (int i = 0; i < newLineIndexes.length; i++) {
      this.newLineIndexes[i] = newLineIndexesBuilder.get(i);
    }
  }

  public char[] input() {
    return inputChars;
  }

  public URI uri() {
    return uri;
  }

  public String substring(int from, int to) {
    StringBuilder sb = new StringBuilder();
    for (int i = from; i < to; i++) {
      sb.append(inputChars[i]);
    }
    return sb.toString();
  }

  public int[] lineAndColumnAt(int index) {
    int[] result = new int[2];
    result[0] = lineAt(index);
    result[1] = index - lineStartIndex(result[0]) + 1;
    return result;
  }

  private int lineAt(int index) {
    int i = Arrays.binarySearch(newLineIndexes, index);
    return i >= 0 ? (i + 2) : -i;
  }

  private int lineStartIndex(int line) {
    return line == 1 ? 0 : newLineIndexes[line - 2];
  }

  /**
   * New lines are: \n, \r\n (in which case true is returned for the \n) and \r alone.
   */
  private static boolean isNewLine(char[] input, int i) {
    return input[i] == '\n' ||
        (input[i] == '\r' && (i + 1 == input.length || input[i + 1] != '\n'));
  }

}
