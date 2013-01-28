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
package org.sonar.sslr.internal.ast.select;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.Iterator;

/**
 * {@link AstSelect} which contains no elements.
 */
public class EmptyAstSelect implements AstSelect {

  public AstSelect children() {
    return this;
  }

  public AstSelect children(AstNodeType type) {
    return this;
  }

  public AstSelect children(AstNodeType... types) {
    return this;
  }

  public AstSelect descendants(AstNodeType type) {
    return this;
  }

  public AstSelect descendants(AstNodeType... types) {
    return this;
  }

  public boolean isEmpty() {
    return true;
  }

  public boolean isNotEmpty() {
    return false;
  }

  public AstSelect filter(Predicate<AstNode> predicate) {
    return this;
  }

  public int size() {
    return 0;
  }

  public AstNode get(int index) {
    throw new IndexOutOfBoundsException();
  }

  public Iterator<AstNode> iterator() {
    return Iterators.emptyIterator();
  }

}
