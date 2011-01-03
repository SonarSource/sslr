/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.List;

import com.sonar.sslr.api.AstNode;

public class PathFinder {

  private final ControlFlowGraph graph;
  private final PathVisitor[] visitors;
  private final ControlFlowStack controlFlowStack = new ControlFlowStack();
  private Statement currentStmt;
  private Block firstBlock;
  private int indexOfFirstStmt;
  private boolean pathFinderStarted = false;

  public PathFinder(ControlFlowGraph graph, PathVisitor... visitors) {
    this.visitors = visitors;
    this.graph = graph;
  }

  public void visitPathsFrom(Block block) {
    visitPathsFrom(block, 0);
  }

  public void visitPathsFrom(Statement stmt) {
    Block block = graph.getBlock(stmt);
    visitPathsFrom(block, block.indexOf(stmt));
  }

  public void visitPathsFrom(AstNode stmtNode) {
    visitPathsFrom(graph.getStatement(stmtNode));
  }

  private void visitPathsFrom(Block block, int indexOfFirstStmt) {
    if ( !pathFinderStarted) {
      this.indexOfFirstStmt = indexOfFirstStmt;
      firstBlock = block;
      return;
    }
    List<Statement> stmts = block.getStatements();
    for (int i = indexOfFirstStmt; i < stmts.size(); i++) {
      currentStmt = stmts.get(i);
      visitStatement();
      if (currentStmt.isControlFlowStatement()) {
        StatementFlowHandler flowHandler = currentStmt.getFlowHandler();
        flowHandler.process(this, controlFlowStack);
        if (flowHandler.shouldStopCurrentPath()) {
          visitEndPath();
          return;
        }
      }
    }
  }

  private void visitEndPath() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].endPath();
    }
  }

  private void visitStatement() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitStatment(currentStmt);
    }
  }

  public void start() {
    pathFinderStarted = true;
    visitStart();
    visitPathsFrom(firstBlock, indexOfFirstStmt);
    while (controlFlowStack.hasBranchesToExplore()) {
      controlFlowStack.peekBranchToExplore().exploreNewBranch(this, controlFlowStack);
    }
    visitEnd();
  }

  private void visitEnd() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].end();
    }
  }

  private void visitStart() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].start();
    }
  }
}
