/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.ast;

import java.util.*;

import com.sonar.sslr.api.*;

public class AstWalker {

  private final Map<AstNodeType, AstVisitor[]> visitorsByNodeType = new IdentityHashMap<AstNodeType, AstVisitor[]>();
  private final List<AstVisitor> visitors = new ArrayList<AstVisitor>();
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
      astAndTokenVisitors = tokenVisitorsList.toArray(new AstAndTokenVisitor[tokenVisitorsList.size()]);
    }
  }

  public void walkAndVisit(AstNode ast) {
    walkVisitAndListen(ast, new Object());
  }

  public void walkVisitAndListen(AstNode ast, Object output) {
    for (AstVisitor visitor : visitors) {
      visitor.visitFile(ast);
    }
    visit(ast, output);
    for (int i = visitors.size() - 1; i >= 0; i--) {
      visitors.get(i).leaveFile(ast);
    }
  }

  private void visit(AstNode ast, Object output) {
    ast.startListening(output);
    AstVisitor[] nodeVisitors = getNodeVisitors(ast);
    visitNode(ast, nodeVisitors);
    visitToken(ast);
    visitChildren(ast, output);
    leaveNode(ast, nodeVisitors);
    ast.stopListening(output);
  }

  private void leaveNode(AstNode ast, AstVisitor[] nodeVisitors) {
    for (int i = nodeVisitors.length - 1; i >= 0; i--) {
      nodeVisitors[i].leaveNode(ast);
    }
  }

  private void visitChildren(AstNode ast, Object output) {
    List<AstNode> children = ast.getChildren();
    if (children != null) {
      for (int i = 0; i < children.size(); i++) {
        visit(ast.getChild(i), output);
      }
    }
  }

  private void visitToken(AstNode ast) {
    if (ast.getToken() != null && lastVisitedToken != ast.getToken()) {
      lastVisitedToken = ast.getToken();
      for (AstAndTokenVisitor astAndTokenVisitor : astAndTokenVisitors) {
        astAndTokenVisitor.visitToken(lastVisitedToken);
      }
    }
  }

  private void visitNode(AstNode ast, AstVisitor[] nodeVisitors) {
    for (AstVisitor nodeVisitor : nodeVisitors) {
      nodeVisitor.visitNode(ast);
    }
  }

  private AstVisitor[] getNodeVisitors(AstNode ast) {
    AstVisitor[] nodeVisitors = visitorsByNodeType.get(ast.getType());
    if (nodeVisitors == null) {
      nodeVisitors = new AstVisitor[0];
    }
    return nodeVisitors;
  }

  private void putAstVisitors(AstNodeType type, List<AstVisitor> visitors) {
    visitorsByNodeType.put(type, visitors.toArray(new AstVisitor[visitors.size()]));
  }

  private List<AstVisitor> getAstVisitors(AstNodeType type) {
    AstVisitor[] visitors = visitorsByNodeType.get(type);
    if (visitors == null) {
      return new ArrayList<AstVisitor>();
    }
    return new ArrayList<AstVisitor>(Arrays.asList(visitors));
  }
}
