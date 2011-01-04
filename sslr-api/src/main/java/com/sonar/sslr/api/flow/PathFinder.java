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

  public void visitPath(Block block) {
    visitPaths(block, 0);
  }

  public void visitPath(Statement stmt) {
    Block block = graph.getBlock(stmt);
    visitPaths(block, block.indexOf(stmt));
  }

  public void visitPath(AstNode stmtNode) {
    visitPath(graph.getStatement(stmtNode));
  }

  private void visitPaths(Block block, int indexOfFirstStmt) {
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
  
  public void visitBranch() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitBranch(currentStmt);
    }
  }
  
  public void leaveBranch() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].leaveBranch();
    }
  }

  public void start() {
    pathFinderStarted = true;
    visitStart();
    visitPaths(firstBlock, indexOfFirstStmt);
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
