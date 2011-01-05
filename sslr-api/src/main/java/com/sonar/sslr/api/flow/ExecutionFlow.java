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

public class ExecutionFlow {

  private Map<Statement, Block> statments = new HashMap<Statement, Block>();
  private Map<AstNode, Statement> stmtAstNodes = new HashMap<AstNode, Statement>();

  public final void addStatement(Block block, Statement stmt) {
    statments.put(stmt, block);
    stmtAstNodes.put(stmt.getAstNode(), stmt);
    block.addStatement(stmt);
  }

  public final Set<Block> getBlocks() {
    return new HashSet<Block>(statments.values());
  }

  public final Block getBlock(Statement stmt) {
    return statments.get(stmt);
  }

  public final Statement getStatement(AstNode stmtNode) {
    return stmtAstNodes.get(stmtNode);
  }
}
