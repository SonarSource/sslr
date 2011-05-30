/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.HashSet;
import java.util.Set;

public class NotVisitTwiceTheSameStatementBarrier extends ExecutionFlowVisitor {

  private Set<Statement> stmtsInThePath = new HashSet<Statement>();

  @Override
  public void start() {
    stmtsInThePath = new HashSet<Statement>();
  }

  @Override
  public void visitStatement(Statement stmt) {
    if (stmtsInThePath.contains(stmt)) {
      throw new BarrierSignal();
    }
    stmtsInThePath.add(stmt);
  }

  @Override
  public void stop() {
    stmtsInThePath = null;
  }

}
