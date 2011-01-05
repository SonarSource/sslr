/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.sonar.sslr.api.AstNode;

public class PathExplorer implements Observer {

  private final ControlFlowGraph graph;
  private final PathVisitor[] visitors;
  private final PathExplorerStack controlFlowStack = new PathExplorerStack();
  private Statement currentStmt;
  private Statement lastEndPathStmt;
  private Block firstBlock;
  private int indexOfFirstStmt;
  private boolean pathFinderStarted = false;

  public PathExplorer(ControlFlowGraph graph, PathVisitor... visitors) {
    this.visitors = visitors;
    for (PathVisitor visitor : visitors) {
      visitor.addObserver(this);
    }
    this.graph = graph;
  }

  public void visit(Block block) {
    visit(block, 0);
  }

  public void visit(Statement stmt) {
    Block block = graph.getBlock(stmt);
    visit(block, block.indexOf(stmt));
  }

  public void visit(AstNode stmtNode) {
    visit(graph.getStatement(stmtNode));
  }

  private void visit(Block block, int indexOfFirstStmt) {
    if ( !pathFinderStarted) {
      this.indexOfFirstStmt = indexOfFirstStmt;
      firstBlock = block;
      return;
    }
    List<Statement> stmts = block.getStatements();
    for (int i = indexOfFirstStmt; i < stmts.size(); i++) {
      currentStmt = stmts.get(i);
      visitStatement();
      if (currentStmt.hasEdges()) {
        Edges flowHandler = currentStmt.getEdgeHandler();
        flowHandler.processPath(this, controlFlowStack);
        if (flowHandler.shouldStopCurrentPath()) {
          visitEndPath();
          return;
        }
      }
    }
    if (block == firstBlock) {
      visitEndPath();
    }
  }

  public void visitEndPath() {
    if (currentStmt != lastEndPathStmt) {
      for (int i = 0; i < visitors.length; i++) {
        visitors[i].endPath();
      }
    }
    lastEndPathStmt = currentStmt;
  }

  private void visitStatement() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitStatement(currentStmt);
    }
  }

  public void visitBranch() {
    for (int i = 0; i < visitors.length; i++) {
      visitors[i].visitBranch();
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
    try {
      visit(firstBlock, indexOfFirstStmt);
    } catch (StopExploring e) {

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

  public void update(Observable o, Object arg) {
    throw new StopExploring();
  }

  private class StopExploring extends RuntimeException {
  }
}
