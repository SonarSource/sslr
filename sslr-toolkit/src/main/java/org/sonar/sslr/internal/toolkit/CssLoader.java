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
package org.sonar.sslr.internal.toolkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CssLoader {

  private static final String CSS_PATH = "/org/sonar/sslr/toolkit/sourceCodeEditor.css";

  private CssLoader() {
  }

  public static String getCss() {
    try {
      return Optional.ofNullable(CssLoader.class.getResourceAsStream(CSS_PATH))
        .map(InputStreamReader::new)
        .map(BufferedReader::new)
        .map(BufferedReader::lines)
        .map(lines -> lines.collect(Collectors.joining("\n")))
        .orElseThrow(() -> new NoSuchElementException("CSS not found by path: " + CSS_PATH));
    } catch (UncheckedIOException e) {
      throw new RuntimeException(e);
    }
  }

}
