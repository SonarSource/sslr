/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.List;

import com.sonar.sslr.api.AstNode;

public class BranchExplorer {

  private final ControlFlowGraph graph;
  private final BranchVisitor[] visitors;
  private final ControlFlowStack controlFlowStack = new ControlFlowStack();
  private Statement currentStmt;
  private Statement lastEndPathStmt;
  private Block firstBlock;
  private int indexOfFirstStmt;
  private boolean pathFinderStarted = false;

  public BranchExplorer(ControlFlowGraph graph, BranchVisitor... visitors) {
    this.visitors = visitors;
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
      if (currentStmt.isControlFlowStatement()) {
        StatementFlowHandler flowHandler = currentStmt.getFlowHandler();
        flowHandler.process(this, controlFlowStack);
        if (flowHandler.shouldStopCurrentPath()) {
          visitEndPath();
          return;
        }
      }
    }
    if(block == firstBlock){
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
    visit(firstBlock, indexOfFirstStmt);
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
