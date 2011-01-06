/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class Statement {

  private final AstNode astNode;
  private FlowHandler flowHandler;
  private Statement nextStmt;
  private Statement previousStmt;

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

  public void setFlowHandler(FlowHandler flowHandler) {
    this.flowHandler = flowHandler;
  }

  public FlowHandler getFlowHandler() {
    return flowHandler;
  }

  public boolean hasFlowHandler() {
    return flowHandler != null;
  }

  public void setNext(Statement nextStmt) {
    this.nextStmt = nextStmt;
    if (nextStmt != null) {
      nextStmt.previousStmt = this;
    }
  }

  public Statement getNext() {
    return nextStmt;
  }

  public Statement getPrevious() {
    return previousStmt;
  }

  public boolean hasNext() {
    return nextStmt != null;
  }
  
  public boolean hasPrevious() {
    return previousStmt != null;
  }
}
