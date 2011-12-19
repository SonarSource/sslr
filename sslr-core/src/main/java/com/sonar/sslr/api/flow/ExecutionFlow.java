/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public interface ExecutionFlow<STATEMENT extends Statement> {

  void visitFlow(AstNode stmtToStartVisitFrom, ExecutionFlowVisitor<STATEMENT>... visitors);

  void visitFlow(ExecutionFlowVisitor<STATEMENT>... visitors);

}
