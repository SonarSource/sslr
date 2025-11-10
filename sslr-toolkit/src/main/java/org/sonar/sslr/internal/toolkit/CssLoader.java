/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public final class CssLoader {

  private static final String CSS_PATH = "/org/sonar/sslr/toolkit/sourceCodeEditor.css";

  private CssLoader() {
  }

  public static String getCss() {
    try {
      InputStream inputStream = CssLoader.class.getResourceAsStream(CSS_PATH);
      return IOUtils.toString(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
