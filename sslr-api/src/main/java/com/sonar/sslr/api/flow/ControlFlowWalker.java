/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.List;
import java.util.Stack;

class ControlFlowWalker {

  private final PathVisitor[] visitors;
  private final Stack<ControlFlowBranch> branches = new Stack<ControlFlowBranch>();
  private final Block firstBlock;
  private final int indexOfFirstStmt;

  ControlFlowWalker(Block firstBlock, int indexOfFirstStmt, PathVisitor... visitors) {
    this.visitors = visitors;
    this.firstBlock = firstBlock;
    this.indexOfFirstStmt = indexOfFirstStmt;
  }

  public void visitPathsFrom(Block block, int index) {
    List<Statement> stmts = block.getStatements();
    for (int i = index; i < stmts.size(); i++) {
      stmts.get(i).handleVisit(this);
    }
  }

  public void visitStatement(Statement stmt) {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitStatment(stmt);
    }
  }

  public void start() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].start();
    }
    visitPathsFrom(firstBlock, indexOfFirstStmt);
    while ( !branches.empty()) {
      branches.pop().getControlFlowStmt().handleVisit(this);
    }
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].end();
    }
  }

  public void endPath() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].endPath();
    }
  }
}
