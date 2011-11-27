/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlowEngine implements ExecutionFlow {

  private ExecutionFlowVisitor<Statement>[] visitors = new ExecutionFlowVisitor[0];
  private FunctionCallStack functionCallStack = new FunctionCallStack();
  private final Stack<Branch> branchStack = new Stack<Branch>();
  private int recursionStackDepth = 0;
  private static final int MAXIMUM_RECURSION_STACK_DEPTH = 200;
  private Statement lastStmt;
  private Statement lastEndPathStmt;
  private Statement firstStmt;
  private boolean executionFlowStarted = false;
  private Map<AstNode, Statement> stmtAstNodes = new HashMap<AstNode, Statement>();

  public final void add(Statement stmt) {
    stmtAstNodes.put(stmt.getAstNode(), stmt);
  }

  public final Statement getStatement(AstNode stmtNode) {
    return stmtAstNodes.get(stmtNode);
  }

  public final void visitFlow(AstNode stmtToStartVisitFrom, ExecutionFlowVisitor... visitors) {
    this.visitors = visitors;
    visitFlow(getStatement(stmtToStartVisitFrom));
    start();
  }

  public final void visitFlow(Statement stmtToStartVisitFrom, ExecutionFlowVisitor... visitors) {
    this.visitors = visitors;
    visitFlow(stmtToStartVisitFrom);
    start();
  }

  public final Collection<Statement> getStatements() {
    return stmtAstNodes.values();
  }

  public final void visitFlow(Statement stmtToStartVisitFrom) {
    if ( !executionFlowStarted) {
      branchStack.push(new Branch());
      this.firstStmt = stmtToStartVisitFrom;
      return;
    }
    if (recursionStackDepth > MAXIMUM_RECURSION_STACK_DEPTH) {
      throw new BarrierSignal();
    }
    recursionStackDepth++;
    Branch branch = getCurrentBranch();
    try {
      Statement currentStmt = stmtToStartVisitFrom;
      while (currentStmt != null) {
        lastStmt = currentStmt;
        callVisitStatementOnVisitors();
        if (currentStmt.hasFlowHandler()) {
          FlowHandler flowHandler = currentStmt.getFlowHandler();
          Statement stmt = flowHandler.processFlow(this);
          if (stmt != null) {
            currentStmt = stmt;
            continue;
          }
        }
        currentStmt = currentStmt.getNext();
      }

      if (firstStmt == stmtToStartVisitFrom) {
        callEndPathOnVisitors();
      }
    } catch (ExecutionFlowSignal signal) {
      while (getCurrentBranch() != branch) {
        branchStack.pop();
        if (branchStack.empty()) {
          throw new IllegalStateException("The SSLR execution of flow engine is unable to recover branch state " + branch);
        }
      }
      throw signal;
    } finally {
      recursionStackDepth--;
    }
  }

  public void callEndPathOnVisitors() {
    if (lastStmt != lastEndPathStmt) {
      for (int i = 0; i < visitors.length; i++) {
        visitors[i].endPath(getCurrentBranch());
      }
    }
    lastEndPathStmt = lastStmt;
  }

  private void callVisitStatementOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitStatement(lastStmt, getCurrentBranch());
    }
  }

  public void callVisitBranchOnVisitors(Statement conditionalStatement, AstNode condition) {
    Branch branch = new Branch(getCurrentBranch());
    branch.setCondition(condition);
    branch.setConditionalStatement(conditionalStatement);
    branchStack.push(branch);

    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitBranch(branch);
    }
  }

  public void callVisitMandatoryBranches() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitMandatoryBranches();
    }
  }

  public void callLeaveMandatoryBranches() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveMandatoryBranches();
    }
  }

  public void callLeaveBranchOnVisitors() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveBranch(getCurrentBranch());
    }
    branchStack.pop();
  }

  private Branch getCurrentBranch() {
    return branchStack.peek();
  }

  final void start() {
    executionFlowStarted = true;
    recursionStackDepth = 0;
    callStartOnVisitors();
    try {
      visitFlow(firstStmt);
    } catch (StopPathExplorationSignal signal) {
    } catch (StopFlowExplorationSignal signal) {
    } catch (BarrierSignal signal) {
    } finally {
      try {
        callEndPathOnVisitors();
      } catch (ExecutionFlowSignal signal) {
      }
      executionFlowStarted = false;
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

  public FunctionCallStack getFunctionCallStack() {
    return functionCallStack;
  }

  public void setFunctionCallStackStack(FunctionCallStack functionCallStack) {
    this.functionCallStack = functionCallStack;
  }

  public void visitFlow(ExecutionFlowVisitor... visitors) {
    throw new UnsupportedOperationException();
  }

  public class FunctionCallStack {

    private Stack<FlowHandler> branches = new Stack<FlowHandler>();

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

    public FunctionCallStack clone() {
      FunctionCallStack clone = new FunctionCallStack();
      clone.branches = (Stack<FlowHandler>) branches.clone();
      return clone;
    }
  }
}
