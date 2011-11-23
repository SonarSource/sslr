/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class RecursionBarrier extends ExecutionFlowVisitor {

  private Set<Statement> stmtsInTheExecutionStack;
  private Stack<Set<Statement>> statementsOnFunctionCall;
  private boolean visitCopyBookOrGeneratedNode;

  public RecursionBarrier(boolean visitCopyBookOrGeneratedNode) {
    this.visitCopyBookOrGeneratedNode = visitCopyBookOrGeneratedNode;
  }

  @Override
  public void start() {
    stmtsInTheExecutionStack = new HashSet<Statement>();
    statementsOnFunctionCall = new Stack<Set<Statement>>();
  }

  @Override
  public void visitFunctionCall(Statement functionCallStmt) {
    statementsOnFunctionCall.add(new HashSet<Statement>());

  }

  @Override
  public void visitStatement(Statement stmt, Branch branch) {
    if (stmtsInTheExecutionStack.contains(stmt) || ( !visitCopyBookOrGeneratedNode && stmt.getAstNode().isCopyBookOrGeneratedNode())) {
      throw new BarrierSignal();
    }
    if ( !statementsOnFunctionCall.empty()) {
      statementsOnFunctionCall.peek().add(stmt);
    }
    stmtsInTheExecutionStack.add(stmt);

  }

  @Override
  public void leaveFunctionCall(Statement functionCallStmt) {
    stmtsInTheExecutionStack.removeAll(statementsOnFunctionCall.pop());
  }

  @Override
  public void stop() {
    stmtsInTheExecutionStack = null;
    stmtsInTheExecutionStack = null;
  }

}
