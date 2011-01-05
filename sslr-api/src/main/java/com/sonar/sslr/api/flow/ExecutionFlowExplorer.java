/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Observable;
import java.util.Observer;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowExplorer implements Observer {

  private final ExecutionFlow executionFlow;
  private final ExecutionFlowVisitor[] visitors;
  private final ExecutionFlowStack executionFlowStack = new ExecutionFlowStack();
  private Statement lastStmt;
  private Statement lastEndPathStmt;
  private Statement firstStmt;
  private boolean executionFlowStarted = false;

  public ExecutionFlowExplorer(ExecutionFlow executionFlow, ExecutionFlowVisitor... visitors) {
    this.visitors = visitors;
    for (ExecutionFlowVisitor visitor : visitors) {
      visitor.addObserver(this);
    }
    this.executionFlow = executionFlow;
  }

  public void visitPath(AstNode stmtNode) {
    visitPath(executionFlow.getStatement(stmtNode));
  }

  public void visitPath(Statement stmt) {
    if ( !executionFlowStarted) {
      this.firstStmt = stmt;
      return;
    }
    Statement currentStmt = stmt;
    do {
      lastStmt = currentStmt;
      callVisitStatementOnVisitors();
      if (currentStmt.hasFlowHandler()) {
        FlowHandler flowHandler = currentStmt.getFlowHandler();
        flowHandler.processFlow(this, executionFlowStack);
        if (flowHandler.shouldStopCurrentPath()) {
          callEndPathOnVisitors();
          return;
        }
      }
      currentStmt = currentStmt.getNext();
    } while (currentStmt != null);
    if (firstStmt == stmt) {
      callEndPathOnVisitors();
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

  public void callLeaveBranchOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveBranch();
    }
  }

  public void start() {
    executionFlowStarted = true;
    callVisitStartOnVisitors();
    try {
      visitPath(firstStmt);
    } catch (StopExploring e) {

    }
    callVisitEndOnVisitors();
  }

  private void callVisitEndOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].end();
    }
  }

  private void callVisitStartOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].start();
    }
  }

  public void update(Observable o, Object arg) {
    throw new StopExploring();
  }

  private class StopExploring extends RuntimeException {
  }
}
