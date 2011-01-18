/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public abstract class ExecutionFlowVisitor<STATEMENT extends Statement<DATASTATES>, DATASTATES extends DataStates> {

  protected DATASTATES dataStates;

  public void start() {
  }
  
  public DATASTATES getDataStates(){
    return dataStates;
  }

  public void visitStatement(STATEMENT stmt) {
  }

  public void visitMandatoryBranches() {
  }

  public void visitBranch() {
  }

  public void leaveBranch() {
  }

  public void leaveMandatoryBranches() {
  }

  public void endPath() {
  }

  public void stop() {
  }
}
