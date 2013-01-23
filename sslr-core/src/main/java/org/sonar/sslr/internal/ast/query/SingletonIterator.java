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
package org.sonar.sslr.internal.ast.query;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.AstNode;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingletonIterator implements Iterator<AstNode> {

  private AstNode node;

  public SingletonIterator(AstNode node) {
    Preconditions.checkNotNull(node);

    this.node = node;
  }

  public boolean hasNext() {
    return node != null;
  }

  public AstNode next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    AstNode result = node;
    node = null;
    return result;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

}
