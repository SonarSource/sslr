/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class Statement {

  private final AstNode stmtAstNode;
  private StatementFlowHandler flowHandler;

  public Statement(AstNode stmtAstNode) {
    this.stmtAstNode = stmtAstNode;
  }

  public AstNode getAstNode() {
    return stmtAstNode;
  }

  @Override
  public String toString() {
    return "Statement (" + stmtAstNode + ")";
  }

  public void setFlowHandler(StatementFlowHandler flowHandler) {
    this.flowHandler = flowHandler;
  }

  public StatementFlowHandler getFlowHandler() {
    return flowHandler;
  }

  public boolean isControlFlowStatement() {
    return flowHandler != null;
  }
}
