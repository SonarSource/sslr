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
import com.sonar.sslr.api.AstNodeType;

import java.util.Iterator;

public class AstQuery {

  private final AstQuery parent;

  public AstQuery() {
    this.parent = null;
  }

  public AstQuery(AstQuery parent) {
    this.parent = parent;
  }

  public AstQuery children() {
    return new ChildrenAstQuery(this);
  }

  public AstQuery children(AstNodeType type) {
    return children().ofType(type);
  }

  public AstQuery children(AstNodeType type1, AstNodeType type2, AstNodeType... remainingTypes) {
    return children().ofType(type1, type2, remainingTypes);
  }

  public AstQuery ofType(AstNodeType type) {
    return new OfTypeAstQuery(this, type);
  }

  private AstQuery ofType(AstNodeType type1, AstNodeType type2, AstNodeType... remainingTypes) {
    return new OfMultipleTypesAstQuery(this, type1, type2, remainingTypes);
  }

  public AstQuery having(AstQuery subQuery) {
    return new ExistsAstQuery(this, subQuery);
  }

  public AstQuery notHaving(AstQuery subQuery) {
    return new NotExistsAstQuery(this, subQuery);
  }

  public Iterator<AstNode> execute(Iterator<AstNode> nodes) {
    if (parent == null) {
      return nodes;
    } else {
      return parent.execute(nodes);
    }
  }

}
