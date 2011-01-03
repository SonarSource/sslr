/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class IfThenElseStatement extends Statement {

  private final AstNode condition;
  private boolean elseBranch = false;

  public IfThenElseStatement(AstNode stmtAstNode, AstNode condition) {
    super(stmtAstNode);
    this.condition = condition;
  }

  public AstNode getCondition() {
    return condition;
  }

  public void setElseBranch() {
    this.elseBranch = true;
  }

  public boolean hasElseBranch() {
    return elseBranch;
  }
}
