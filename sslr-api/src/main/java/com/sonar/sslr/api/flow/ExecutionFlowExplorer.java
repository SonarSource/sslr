/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Stack;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowExplorer<STATEMENT extends Statement<? extends DataStates>> {

  private final ExecutionFlow<STATEMENT> executionFlow;
  private final ExecutionFlowVisitor<STATEMENT>[] visitors;
  private final FlowStack executionFlowStack = new FlowStack();
  private STATEMENT lastStmt;
  private STATEMENT lastEndPathStmt;
  private STATEMENT firstStmt;
  private boolean executionFlowStarted = false;

  ExecutionFlowExplorer(ExecutionFlow<STATEMENT> executionFlow, ExecutionFlowVisitor<STATEMENT>... visitors) {
    this.visitors = visitors;
    this.executionFlow = executionFlow;
  }

  public void visitFlow(AstNode stmtToStartVisitFrom) {
    visitFlow(executionFlow.getStatement(stmtToStartVisitFrom));
  }

  public void visitFlow(STATEMENT stmtToStartVisitFrom) {
    if ( !executionFlowStarted) {
      this.firstStmt = stmtToStartVisitFrom;
      return;
    }
    try {
      STATEMENT currentStmt = stmtToStartVisitFrom;
      while (currentStmt != null) {
        lastStmt = currentStmt;
        callVisitStatementOnVisitors();
        if (currentStmt.hasFlowHandler()) {
          try {
            FlowHandler flowHandler = currentStmt.getFlowHandler();
            flowHandler.processFlow(this);
          } catch (EndPathSignal signal) {
            callEndPathOnVisitors();
            return;
          }
        }
        currentStmt = (STATEMENT) currentStmt.getNext();
      }
      if (firstStmt == stmtToStartVisitFrom) {
        callEndPathOnVisitors();
      }
    } catch (StopPathExplorationSignal signal) {
      return;
    }
  }

  public void callEndPathOnVisitors() {
    if (lastStmt != lastEndPathStmt) {
      for (int i = 0; i < visitors.length; i++) {
        visitors[i].endPath();
      }
    }
    lastEndPathStmt = lastStmt;
  }

  private void callVisitStatementOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitStatement(lastStmt);
    }
  }

  public void callVisitBranchOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitBranch();
    }
  }

  public void callStartVisitingMandatoryBranches() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].startVisitingMandatoryBranches();
    }
  }
  
  public void callStopVisitingMandatoryBranches() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].stopVisitingMandatoryBranches();
    }
  }

  public void callLeaveBranchOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveBranch();
    }
  }

  void start() {
    executionFlowStarted = true;
    callVisitStartOnVisitors();
    try {
      visitFlow(firstStmt);
    } catch (StopFlowExplorationSignal e) {

    }
    callVisitEndOnVisitors();
  }

  private void callVisitEndOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].stop();
    }
  }

  private void callVisitStartOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].start();
    }
  }

  public FlowStack getExecutionFlowStack() {
    return executionFlowStack;
  }

  public class FlowStack {

    private final Stack<FlowHandler> branches = new Stack<FlowHandler>();

    public final boolean isEmpty() {
      return branches.isEmpty();
    }

    public final void add(FlowHandler flowHandler) {
      branches.push(flowHandler);
    }

    public final FlowHandler peek() {
      return branches.peek();
    }

    public FlowHandler pop() {
      return branches.pop();
    }

    public boolean contains(FlowHandler flowHandler) {
      return branches.contains(flowHandler);
    }
  }
}
