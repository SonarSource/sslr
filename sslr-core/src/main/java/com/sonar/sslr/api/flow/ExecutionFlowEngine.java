/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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
  private Stack<Branch> branchStack = new Stack<Branch>();
  private int visitStackDepth = 0;
  private static final int MAXIMUM_VISIT_STACK_DEPTH = 200;
  private Statement lastStmt;
  private Statement lastEndPathStmt;
  private Statement firstStmt;
  private boolean executionFlowStarted = false;
  private final Map<AstNode, Statement> stmtAstNodes = new HashMap<AstNode, Statement>();

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
      branchStack = new Stack<Branch>();
      functionCallStack = new FunctionCallStack();
      branchStack.push(new Branch());
      this.firstStmt = stmtToStartVisitFrom;
      return;
    }
    if (visitStackDepth > MAXIMUM_VISIT_STACK_DEPTH) {
      throw new BarrierSignal();
    }
    visitStackDepth++;
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
      visitStackDepth--;
    }
  }

  public void callEndPathOnVisitors() {
    if (lastStmt != lastEndPathStmt) {
      for (ExecutionFlowVisitor<Statement> visitor : visitors) {
        visitor.endPath(getCurrentBranch());
      }
    }
    lastEndPathStmt = lastStmt;
  }

  private void callVisitStatementOnVisitors() {
    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.visitStatement(lastStmt, getCurrentBranch());
    }
  }

  public void callVisitBranchOnVisitors(Statement conditionalStatement, AstNode condition) {
    Branch branch = new Branch(getCurrentBranch());
    branch.setCondition(condition);
    branch.setConditionalStatement(conditionalStatement);
    branchStack.push(branch);

    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.visitBranch(branch);
    }
  }

  public void callVisitMandatoryBranches() {
    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.visitMandatoryBranches();
    }
  }

  public void callLeaveMandatoryBranches() {
    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.leaveMandatoryBranches();
    }
  }

  public void callLeaveBranchOnVisitors() {
    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.leaveBranch(getCurrentBranch());
    }
    branchStack.pop();
  }

  private Branch getCurrentBranch() {
    return branchStack.peek();
  }

  final void start() {
    executionFlowStarted = true;
    visitStackDepth = 0;
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
      branchStack = new Stack<Branch>();
      functionCallStack = new FunctionCallStack();
    }
    callStopOnVisitors();
    visitors = null;
  }

  private void callStopOnVisitors() {
    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.stop();
    }
  }

  private void callStartOnVisitors() {
    for (ExecutionFlowVisitor<Statement> visitor : visitors) {
      visitor.start();
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

  public final class FunctionCallStack implements Cloneable {

    private Stack<FlowHandler> branches = new Stack<FlowHandler>();

    public boolean isEmpty() {
      return branches.isEmpty();
    }

    public void add(FlowHandler flowHandler) {
      branches.push(flowHandler);
    }

    public FlowHandler peek() {
      return branches.peek();
    }

    public FlowHandler pop() {
      return branches.pop();
    }

    public boolean contains(FlowHandler flowHandler) {
      return branches.contains(flowHandler);
    }

    @Override
    public FunctionCallStack clone() {
      FunctionCallStack clone = new FunctionCallStack();
      clone.branches = (Stack<FlowHandler>) branches.clone();
      return clone;
    }
  }

}
