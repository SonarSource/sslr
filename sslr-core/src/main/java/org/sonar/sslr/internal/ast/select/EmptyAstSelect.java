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
package org.sonar.sslr.internal.ast.select;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.sslr.ast.AstSelect;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * {@link AstSelect} which contains no elements.
 */
public class EmptyAstSelect implements AstSelect {

  @Override
  public AstSelect children() {
    return this;
  }

  @Override
  public AstSelect children(AstNodeType type) {
    return this;
  }

  @Override
  public AstSelect children(AstNodeType... types) {
    return this;
  }

  @Override
  public AstSelect nextSibling() {
    return this;
  }

  @Override
  public AstSelect previousSibling() {
    return this;
  }

  @Override
  public AstSelect parent() {
    return this;
  }

  @Override
  public AstSelect firstAncestor(AstNodeType type) {
    return this;
  }

  @Override
  public AstSelect firstAncestor(AstNodeType... types) {
    return this;
  }

  @Override
  public AstSelect descendants(AstNodeType type) {
    return this;
  }

  @Override
  public AstSelect descendants(AstNodeType... types) {
    return this;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean isNotEmpty() {
    return false;
  }

  @Override
  public AstSelect filter(AstNodeType type) {
    return this;
  }

  @Override
  public AstSelect filter(AstNodeType... types) {
    return this;
  }

  @Override
  public AstSelect filter(Predicate<AstNode> predicate) {
    return this;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public AstNode get(int index) {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public Iterator<AstNode> iterator() {
    return Collections.emptyIterator();
  }

}
