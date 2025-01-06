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
package org.sonar.sslr.examples.grammars.typed.impl;

import javax.annotation.Nullable;
import org.sonar.sslr.examples.grammars.typed.api.SyntaxToken;

public class SyntaxList<T> {

  private T element;
  private SyntaxToken commaToken;
  private SyntaxList<T> next;

  public SyntaxList(T element, @Nullable SyntaxToken commaToken, @Nullable SyntaxList<T> next) {
    this.element = element;
    this.commaToken = commaToken;
    this.next = next;
  }

  public T element() {
    return element;
  }

  @Nullable
  public SyntaxToken commaToken() {
    return commaToken;
  }

  @Nullable
  public SyntaxList next() {
    return next;
  }
}
