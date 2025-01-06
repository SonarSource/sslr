/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.impl.ast;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.AstVisitor;
import com.sonar.sslr.api.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class AstWalker {

  private final Map<AstNodeType, AstVisitor[]> visitorsByNodeType = new IdentityHashMap<>();
  private final List<AstVisitor> visitors = new ArrayList<>();
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
      List<AstAndTokenVisitor> tokenVisitorsList = new ArrayList<>(Arrays.asList(astAndTokenVisitors));
      tokenVisitorsList.add((AstAndTokenVisitor) visitor);
      astAndTokenVisitors = tokenVisitorsList.toArray(new AstAndTokenVisitor[tokenVisitorsList.size()]);
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

  /**
   * @deprecated in 1.18, use {@link #walkAndVisit(AstNode)} instead
   */
  @Deprecated
  public void walkVisitAndListen(AstNode ast, Object output) {
    walkAndVisit(ast);
  }

  private void visit(AstNode ast) {
    AstVisitor[] nodeVisitors = getNodeVisitors(ast);
    visitNode(ast, nodeVisitors);
    visitToken(ast);
    visitChildren(ast);
    leaveNode(ast, nodeVisitors);
  }

  private static void leaveNode(AstNode ast, AstVisitor[] nodeVisitors) {
    for (int i = nodeVisitors.length - 1; i >= 0; i--) {
      nodeVisitors[i].leaveNode(ast);
    }
  }

  private void visitChildren(AstNode ast) {
    for (AstNode child : ast.getChildren()) {
      visit(child);
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

  private static void visitNode(AstNode ast, AstVisitor[] nodeVisitors) {
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
    AstVisitor[] visitorsByType = visitorsByNodeType.get(type);
    return visitorsByType == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(visitorsByType));
  }
}
