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

public class ExecutionFlowExplorer implements Observer {

  private final ExecutionFlow graph;
  private final ExecutionFlowVisitor[] visitors;
  private final ExecutionFlowStack controlFlowStack = new ExecutionFlowStack();
  private Statement currentStmt;
  private Statement lastEndPathStmt;
  private Block firstBlock;
  private int indexOfFirstStmt;
  private boolean pathFinderStarted = false;

  public ExecutionFlowExplorer(ExecutionFlow graph, ExecutionFlowVisitor... visitors) {
    this.visitors = visitors;
    for (ExecutionFlowVisitor visitor : visitors) {
      visitor.addObserver(this);
    }
    this.graph = graph;
  }

  public void visitPath(Block block) {
    visitPath(block, 0);
  }

  public void visitPath(Statement stmt) {
    Block block = graph.getBlock(stmt);
    visitPath(block, block.indexOf(stmt));
  }

  public void visitPath(AstNode stmtNode) {
    visitPath(graph.getStatement(stmtNode));
  }

  private void visitPath(Block block, int indexOfFirstStmt) {
    if ( !pathFinderStarted) {
      this.indexOfFirstStmt = indexOfFirstStmt;
      firstBlock = block;
      return;
    }
    List<Statement> stmts = block.getStatements();
    for (int i = indexOfFirstStmt; i < stmts.size(); i++) {
      currentStmt = stmts.get(i);
      visitStatement();
      if (currentStmt.hasFlowHandler()) {
        FlowHandler flowHandler = currentStmt.getFlowHandler();
        flowHandler.processFlow(this, controlFlowStack);
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
      visitPath(firstBlock, indexOfFirstStmt);
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
