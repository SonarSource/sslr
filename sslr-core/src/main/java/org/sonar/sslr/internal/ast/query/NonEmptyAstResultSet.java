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

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.util.Iterator;
import java.util.List;

public class NonEmptyAstResultSet implements AstResultSet {

  private final List<AstNode> results = Lists.newArrayList();
  private final Iterator<AstNode> resultIterator;

  public NonEmptyAstResultSet(AstNode node, AstQuery query) {
    resultIterator = query.execute(new SingletonIterator(node));
  }

  private NonEmptyAstResultSet(NonEmptyAstResultSet parent, AstQuery query) {
    resultIterator = query.execute(parent.iterator());
  }

  public boolean isEmpty() {
    return hasSize(0);
  }

  public boolean hasSize(int expectedSize) {
    if (expectedSize < results.size()) {
      return false;
    }

    if (resultIterator.hasNext()) {
      Iterator<AstNode> it = iterator();
      while (results.size() <= expectedSize && it.hasNext()) {
        it.next();
      }
    }
    return results.size() == expectedSize;
  }

  public AstResultSet select(AstQuery query) {
    if (isEmpty()) {
      return EmptyResultSet.INSTANCE;
    } else {
      return new NonEmptyAstResultSet(this, query);
    }
  }

  private final Iterator<AstNode> iterator() {
    return new Iterator<AstNode>() {

      private int i = 0;

      public boolean hasNext() {
        return i < results.size() || resultIterator.hasNext();
      }

      public AstNode next() {
        if (i == results.size()) {
          results.add(resultIterator.next());
        }

        return results.get(i++);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

}
