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
}
