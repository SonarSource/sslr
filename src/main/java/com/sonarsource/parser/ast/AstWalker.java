/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonarsource.parser.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AstWalker {

  private Map<AstNodeType, AstVisitor[]> visitorsByNodeType = new IdentityHashMap<AstNodeType, AstVisitor[]>();
  private Set<AstVisitor> visitors = new HashSet<AstVisitor>();

  public void addVisitor(AstVisitor visitor) {
    visitors.add(visitor);
    for (AstNodeType type : visitor.getAstNodeTypes()) {
      List<AstVisitor> visitorsByType = getAstVisitors(type);
      visitorsByType.add(visitor);
      putAstVisitors(type, visitorsByType);
    }
  }

  public void walkAndVisit(AstNode ast) {
    for (AstVisitor visitor : visitors) {
      visitor.visitFile(ast);
    }
    visit(ast);
    for (AstVisitor visitor : visitors) {
      visitor.leaveFile(ast);
    }
  }

  private void visit(AstNode ast) {
    AstVisitor[] nodeVisitors = visitorsByNodeType.get(ast.type);
    if (nodeVisitors == null) {
      nodeVisitors = new AstVisitor[0];
    }
    for (AstVisitor visitor : nodeVisitors) {
      visitor.visitNode(ast);
    }
    if (ast.getChildren() != null) {
      for (AstNode nodeChild : ast.getChildren()) {
        visit(nodeChild);
      }
    }
    for (AstVisitor visitor : nodeVisitors) {
      visitor.leaveNode(ast);
    }
  }

  private void putAstVisitors(AstNodeType type, List<AstVisitor> visitors) {
    visitorsByNodeType.put(type, visitors.toArray(new AstVisitor[0]));
  }

  private List<AstVisitor> getAstVisitors(AstNodeType type) {
    AstVisitor[] visitors = visitorsByNodeType.get(type);
    if (visitors == null) {
      return new ArrayList<AstVisitor>();
    }
    return Arrays.asList(visitors);
  }
}
