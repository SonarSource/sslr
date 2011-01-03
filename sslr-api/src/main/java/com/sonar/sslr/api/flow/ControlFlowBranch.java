/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public class ControlFlowBranch {

  private final Statement controlFlowStmt;
  private ControlFlowBranchState state = null;

  ControlFlowBranch(Statement controlFlowStmt) {
    this.controlFlowStmt = controlFlowStmt;
  }

  public void setState(ControlFlowBranchState state) {
    this.state = state;
  }

  public ControlFlowBranchState getState() {
    return state;
  }
  
  Statement getControlFlowStmt(){
    return controlFlowStmt;
  }

  interface ControlFlowBranchState {
  };
}
