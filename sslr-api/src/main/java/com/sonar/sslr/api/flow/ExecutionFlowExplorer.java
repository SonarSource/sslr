/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Stack;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowExplorer<STATEMENT extends Statement<DATASTATES>, DATASTATES extends DataStates> {

  private final ExecutionFlow<STATEMENT, DATASTATES> executionFlow;
  private final ExecutionFlowVisitor<STATEMENT, DATASTATES>[] visitors;
  private final FlowStack executionFlowStack = new FlowStack();
  private STATEMENT lastStmt;
  private STATEMENT lastEndPathStmt;
  private STATEMENT firstStmt;
  private DATASTATES dataStates = null;
  private boolean executionFlowStarted = false;

  ExecutionFlowExplorer(ExecutionFlow<STATEMENT, DATASTATES> executionFlow, ExecutionFlowVisitor<STATEMENT, DATASTATES>... visitors) {
    this.visitors = visitors;
    this.executionFlow = executionFlow;
  }

  public void setDataStates(DATASTATES dataStates) {
    this.dataStates = dataStates;
    for (ExecutionFlowVisitor<STATEMENT, DATASTATES> visitor : visitors) {
      visitor.dataStates = dataStates;
    }
  }

  public void visitFlow(AstNode stmtToStartVisitFrom) {
    visitFlow(executionFlow.getStatement(stmtToStartVisitFrom));
  }

  public void visitFlow(STATEMENT stmtToStartVisitFrom) {
    if ( !executionFlowStarted) {
      this.firstStmt = stmtToStartVisitFrom;
      return;
    }
    STATEMENT currentStmt = stmtToStartVisitFrom;
    while (currentStmt != null) {
      lastStmt = currentStmt;
      callVisitStatementOnVisitors();
      if (currentStmt.hasFlowHandler()) {
        FlowHandler flowHandler = currentStmt.getFlowHandler();
        flowHandler.processFlow(this);
      }
      currentStmt = (STATEMENT) currentStmt.getNext();
    }
    if (firstStmt == stmtToStartVisitFrom) {
      callEndPathOnVisitors();
    }
  }

  public void callEndPathOnVisitors() {
    if (dataStates != null) {
      dataStates.endPath();
    }
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
    if (dataStates != null) {
      dataStates.visitBranch();
    }
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitBranch();
    }
  }

  public void callVisitMandatoryBranches() {
    if (dataStates != null) {
      dataStates.visitMandatoryBranches();
    }
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitMandatoryBranches();
    }
  }

  public void callLeaveMandatoryBranches() {
    if (dataStates != null) {
      dataStates.leaveMandatoryBranches();
    }
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveMandatoryBranches();
    }
  }

  public void callLeaveBranchOnVisitors() {
    if (dataStates != null) {
      dataStates.leaveBranch();
    }
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveBranch();
    }
  }

  void start() {
    executionFlowStarted = true;
    callStartOnVisitors();
    try {
      try {
        visitFlow(firstStmt);
      } catch (EndPathSignal e) {
        callEndPathOnVisitors();
      }
    } catch (StopPathExplorationSignal e) {

    } catch (StopFlowExplorationSignal e) {

    }
    callStopOnVisitors();
  }

  private void callStopOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].stop();
    }
  }

  private void callStartOnVisitors() {
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
