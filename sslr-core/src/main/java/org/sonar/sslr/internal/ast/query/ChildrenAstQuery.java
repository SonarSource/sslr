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

import com.sonar.sslr.api.AstNode;

import java.util.Iterator;

public class ChildrenAstQuery extends AstQuery {

  // TODO Dinesh: Empty iterator
  private static final Iterator<AstNode> EMPTY_ITERATOR = new EmptyIterator();

  public ChildrenAstQuery() {
    super();
  }

  public ChildrenAstQuery(AstQuery parent) {
    super(parent);
  }

  @Override
  public Iterator<AstNode> execute(Iterator<AstNode> nodes) {
    final Iterator<AstNode> input = super.execute(nodes);

    return new Iterator<AstNode>() {

      private Iterator<AstNode> currentIterator = EMPTY_ITERATOR;

      // TODO Dinesh This is ugly
      {
        seekTillNext();
      }

      public boolean hasNext() {
        return currentIterator.hasNext();
      }

      public AstNode next() {
        AstNode result = currentIterator.next();

        seekTillNext();

        return result;
      }

      private void seekTillNext() {
        while (!currentIterator.hasNext() && input.hasNext()) {
          currentIterator = input.next().getChildren().iterator();
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

}
