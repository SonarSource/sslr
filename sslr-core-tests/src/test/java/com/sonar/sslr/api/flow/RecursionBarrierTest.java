/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;

import org.junit.Test;

public class RecursionBarrierTest {

  RecursionBarrier barrier = new RecursionBarrier(false);

  @Test
  public void shouldStopExecutionWhenEncounteringPreviousVisitedStatement() {
    barrier.start();
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    Statement stmt2 = new Statement(createAstNode("myStmt2"));

    barrier.visitStatement(stmt1, null);
    barrier.visitStatement(stmt2, null);

    try {
      barrier.visitStatement(stmt1, null);
    } catch (BarrierSignal signal) {
      return;
    }
    throw new AssertionError("A BarrierSignal was expected.");
  }
}
