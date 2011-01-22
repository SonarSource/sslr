/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;

import org.junit.Test;

public class NotVisitTwiceTheSameStatementBarrierTest {

  NotVisitTwiceTheSameStatementBarrier barrier = new NotVisitTwiceTheSameStatementBarrier();

  @Test
  public void shouldStopExecutionWhenEncounteringPreviousVisitedStatement() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    Statement stmt2 = new Statement(createAstNode("myStmt2"));

    barrier.visitStatement(stmt1);
    barrier.visitStatement(stmt2);

    try {
      barrier.visitStatement(stmt1);
    } catch (BarrierSignal signal) {
      return;
    }
    throw new AssertionError("A StopPathExplorationSignal was expected.");
  }
}
