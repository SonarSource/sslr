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
package org.sonar.sslr.internal.matchers;

import java.io.File;
import java.net.URI;
import java.util.Objects;

import javax.annotation.Nullable;

class TextLocation {

  private final File file;
  private final URI uri;
  private final int line;
  private final int column;

  public TextLocation(@Nullable File file, @Nullable URI uri, int line, int column) {
    this.file = file;
    this.uri = uri;
    this.line = line;
    this.column = column;
  }

  public File getFile() {
    return file;
  }

  /**
   * For internal use only.
   */
  public URI getFileURI() {
    return uri;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, line, column);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof TextLocation) {
      TextLocation other = (TextLocation) obj;
      return Objects.equals(this.file, other.file)
        && this.line == other.line
        && this.column == other.column;
    }
    return false;
  }

  @Override
  public String toString() {
    return "TextLocation{" + "file=" + file + ", line=" + line + ", column=" + column + '}';
  }

}
