/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class Statement {

  private final AstNode stmtAstNode;

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

  public void handleVisit(ControlFlowWalker flowWalker) {
    flowWalker.visitStatement(this);
  }
}
