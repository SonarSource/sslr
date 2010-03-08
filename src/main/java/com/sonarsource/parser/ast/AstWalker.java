/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
    for (AstNodeType type : visitor.getAstNodeTypesToVisit()) {
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
