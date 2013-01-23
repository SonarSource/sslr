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

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class OfMultipleTypesAstQuery extends AstQuery {

  private final Set<AstNodeType> types;

  public OfMultipleTypesAstQuery(AstNodeType type1, AstNodeType type2, AstNodeType... remainingTypes) {
    this(null, type1, type2, remainingTypes);
  }

  public OfMultipleTypesAstQuery(AstQuery parent, AstNodeType type1, AstNodeType type2, AstNodeType... remainingTypes) {
    super(parent);
    this.types = (Set) ImmutableSet.builder().add(type1).add(type2).addAll(Arrays.asList(remainingTypes)).build();
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

          if (types.contains(element.getType())) {
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
