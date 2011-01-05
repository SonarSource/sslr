/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class Statement {

  private final AstNode astNode;
  private Edges edgeHandler;

  public Statement(AstNode stmtAstNode) {
    this.astNode = stmtAstNode;
  }

  public AstNode getAstNode() {
    return astNode;
  }

  @Override
  public String toString() {
    return "Statement (" + astNode + ")";
  }

  public void setEdgeHandler(Edges edgeHandler) {
    this.edgeHandler = edgeHandler;
  }

  public Edges getEdgeHandler() {
    return edgeHandler;
  }

  public boolean hasEdges() {
    return edgeHandler != null;
  }
}
