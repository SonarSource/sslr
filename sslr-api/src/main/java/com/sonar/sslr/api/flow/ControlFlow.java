/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sonar.sslr.api.AstNode;

public class ControlFlow {

  private Map<Statement, Block> statments = new HashMap<Statement, Block>();
  private Map<AstNode, Statement> stmtAstNodes = new HashMap<AstNode, Statement>();

  public void addStatement(Block block, Statement stmt) {
    statments.put(stmt, block);
    stmtAstNodes.put(stmt.getAstNode(), stmt);
    block.addStatement(stmt);
  }

  public Set<Block> getBlocks() {
    return new HashSet<Block>(statments.values());
  }

  public void visitPathsFrom(AstNode stmt, PathVisitor... visitors) {
    visitPathsFrom(stmtAstNodes.get(stmt), visitors);
  }

  public void visitPathsFrom(Statement stmt, PathVisitor... visitors) {
    Block block = statments.get(stmt);
    ControlFlowWalker walker = new ControlFlowWalker(block, block.indexOf(stmt), visitors);
    walker.start();
  }
}
