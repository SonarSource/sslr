/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.HashMap;
import java.util.Map;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlow {

  private Map<AstNode, Statement> stmtAstNodes = new HashMap<AstNode, Statement>();

  public final void add(Statement stmt) {
    stmtAstNodes.put(stmt.getAstNode(), stmt);
  }

  public final Statement getStatement(AstNode stmtNode) {
    return stmtAstNodes.get(stmtNode);
  }

  public final void visitPath(AstNode stmt, ExecutionFlowVisitor... visitors) {
    ExecutionFlowExplorer explorer = new ExecutionFlowExplorer(this, visitors);
    explorer.visitPath(stmt);
    explorer.start();
  }
}
