/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sonar.sslr.api.AstNode;

public class ExecutionFlow<STATEMENT extends Statement<DATASTATES>, DATASTATES extends DataStates> {

  private Map<AstNode, STATEMENT> stmtAstNodes = new HashMap<AstNode, STATEMENT>();

  public final void add(STATEMENT stmt) {
    stmtAstNodes.put(stmt.getAstNode(), stmt);
  }

  public final STATEMENT getStatement(AstNode stmtNode) {
    return stmtAstNodes.get(stmtNode);
  }

  public final void visitFlow(AstNode stmtToStartVisitFrom, ExecutionFlowVisitor<STATEMENT, DATASTATES>... visitors) {
    ExecutionFlowExplorer<STATEMENT, DATASTATES> explorer = new ExecutionFlowExplorer<STATEMENT, DATASTATES>(this, visitors);
    explorer.visitFlow(stmtToStartVisitFrom);
    explorer.start();
  }

  public final void visitFlow(STATEMENT stmtToStartVisitFrom, ExecutionFlowVisitor<STATEMENT, DATASTATES>... visitors) {
    ExecutionFlowExplorer<STATEMENT, DATASTATES> explorer = new ExecutionFlowExplorer<STATEMENT, DATASTATES>(this, visitors);
    explorer.visitFlow(stmtToStartVisitFrom);
    explorer.start();
  }

  public final void visitFlowAndData(STATEMENT stmtToStartVisitFrom, DATASTATES dataStates,
      ExecutionFlowVisitor<STATEMENT, DATASTATES>... visitors) {
    ExecutionFlowExplorer<STATEMENT, DATASTATES> explorer = new ExecutionFlowExplorer<STATEMENT, DATASTATES>(this, visitors);
    explorer.visitFlow(stmtToStartVisitFrom);
    explorer.setDataStates(dataStates);
    explorer.start();
  }

  public final Collection<STATEMENT> getStatements() {
    return stmtAstNodes.values();
  }
}
