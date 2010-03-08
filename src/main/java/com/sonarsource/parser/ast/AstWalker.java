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

import com.sonarsource.lexer.Token;

public class AstWalker {

  private Map<AstNodeType, AstVisitor[]> visitorsByNodeType = new IdentityHashMap<AstNodeType, AstVisitor[]>();
  private Set<AstVisitor> visitors = new HashSet<AstVisitor>();
  private AstAndTokenVisitor[] astAndTokenVisitors = new AstAndTokenVisitor[0];
  private Token lastVisitedToken = null;

  public AstWalker(AstVisitor... visitors) {
    for (AstVisitor visitor : visitors) {
      addVisitor(visitor);
    }
  }

  public void addVisitor(AstVisitor visitor) {
    visitors.add(visitor);
    for (AstNodeType type : visitor.getAstNodeTypesToVisit()) {
      List<AstVisitor> visitorsByType = getAstVisitors(type);
      visitorsByType.add(visitor);
      putAstVisitors(type, visitorsByType);
    }
    if (visitor instanceof AstAndTokenVisitor) {
      List<AstAndTokenVisitor> tokenVisitorsList = new ArrayList<AstAndTokenVisitor>(Arrays.asList(astAndTokenVisitors));
      tokenVisitorsList.add((AstAndTokenVisitor) visitor);
      astAndTokenVisitors = tokenVisitorsList.toArray(new AstAndTokenVisitor[0]);
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
    if (ast.getToken() != null && lastVisitedToken != ast.getToken()) {
      lastVisitedToken = ast.getToken();
      for (AstAndTokenVisitor astAndTokenVisitor : astAndTokenVisitors) {
        astAndTokenVisitor.visitToken(lastVisitedToken);
      }
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
