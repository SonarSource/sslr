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

  private Set<Statement> stmtsInThePath = new HashSet<Statement>();
  private Stack<Set<Statement>> stmtsByBranches = new Stack<Set<Statement>>();

  @Override
  public void start() {
    stmtsInThePath = new HashSet<Statement>();
    stmtsByBranches = new Stack<Set<Statement>>();
  }

  @Override
  public void visitStatement(Statement stmt) {
    if (stmtsInThePath.contains(stmt)) {
      throw new StopRecursionSignal();
    }
    stmtsInThePath.add(stmt);
    if ( !stmtsByBranches.isEmpty()) {
      stmtsByBranches.peek().add(stmt);
    }
  }

  @Override
  public void visitBranch() {
    stmtsByBranches.push(new HashSet<Statement>());
  }

  @Override
  public void leaveBranch() {
    stmtsInThePath.removeAll(stmtsByBranches.pop());
  }

  @Override
  public void stop() {
    stmtsInThePath = null;
    stmtsByBranches = null;
  }

}
