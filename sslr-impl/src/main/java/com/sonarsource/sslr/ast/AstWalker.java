/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.sonarsource.sslr.api.AstAndTokenVisitor;
import com.sonarsource.sslr.api.AstNode;
import com.sonarsource.sslr.api.AstNodeType;
import com.sonarsource.sslr.api.AstVisitor;
import com.sonarsource.sslr.api.Token;

public class AstWalker {

  private Map<AstNodeType, AstVisitor[]> visitorsByNodeType = new IdentityHashMap<AstNodeType, AstVisitor[]>();
  private List<AstVisitor> visitors = new ArrayList<AstVisitor>();
  private AstAndTokenVisitor[] astAndTokenVisitors = new AstAndTokenVisitor[0];
  private Token lastVisitedToken = null;

  public AstWalker(AstVisitor... visitors) {
    this(Arrays.asList(visitors));
  }

  public AstWalker(List<? extends AstVisitor> visitors) {
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
    for (int i = visitors.size() - 1; i >= 0; i--) {
      visitors.get(i).leaveFile(ast);
    }
  }

  private void visit(AstNode ast) {
    AstVisitor[] nodeVisitors = visitorsByNodeType.get(ast.getType());
    if (nodeVisitors == null) {
      nodeVisitors = new AstVisitor[0];
    }
    for (int i = 0; i < nodeVisitors.length; i++) {
      nodeVisitors[i].visitNode(ast);
    }
    if (ast.getToken() != null && lastVisitedToken != ast.getToken()) {
      lastVisitedToken = ast.getToken();
      for (int i = 0; i < astAndTokenVisitors.length; i++) {
        astAndTokenVisitors[i].visitToken(lastVisitedToken);
      }
    }
    if (ast.getChildren() != null) {
      for (AstNode nodeChild : ast.getChildren()) {
        visit(nodeChild);
      }
    }
    for (int i = nodeVisitors.length - 1; i >= 0; i--) {
      nodeVisitors[i].leaveNode(ast);
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
    return new ArrayList<AstVisitor>(Arrays.asList(visitors));
  }
}
