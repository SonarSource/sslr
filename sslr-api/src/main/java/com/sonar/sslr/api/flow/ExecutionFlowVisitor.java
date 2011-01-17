/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public abstract class ExecutionFlowVisitor<STATEMENT extends Statement<? extends DataStates>> {

  public void start() {
  }

  public void visitStatement(STATEMENT stmt) {
  }

  public void visitBranch() {
  }

  public void leaveBranch() {
  }

  public void endPath() {
  }

  public void end() {
  }
}
