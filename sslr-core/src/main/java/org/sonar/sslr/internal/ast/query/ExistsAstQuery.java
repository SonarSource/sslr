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
import java.util.NoSuchElementException;

public class ExistsAstQuery extends AstQuery {

  private final AstQuery subQuery;

  public ExistsAstQuery(AstQuery subQuery) {
    this(null, subQuery);
  }

  public ExistsAstQuery(AstQuery parent, AstQuery subQuery) {
    super(parent);
    this.subQuery = subQuery;
  }

  @Override
  public Iterator<AstNode> execute(Iterator<AstNode> nodes) {
    final Iterator<AstNode> input = super.execute(nodes);

    return new Iterator<AstNode>() {

      private AstNode current = getCurrent();

      public boolean hasNext() {
        return current != null;
      }

      public AstNode next() {
        if (current == null) {
          throw new NoSuchElementException();
        }

        AstNode result = current;
        current = getCurrent();

        return result;
      }

      private AstNode getCurrent() {
        while (input.hasNext()) {
          AstNode element = input.next();

          if (!element.select(subQuery).isEmpty()) {
            return element;
          }
        }

        return null;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

}
