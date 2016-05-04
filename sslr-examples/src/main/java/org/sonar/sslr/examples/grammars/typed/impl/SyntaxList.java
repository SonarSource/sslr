/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
