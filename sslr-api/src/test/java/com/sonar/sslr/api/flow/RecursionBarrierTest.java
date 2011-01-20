/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import static com.sonar.sslr.api.AstNodeUtils.createAstNode;

import org.junit.Test;

public class RecursionBarrierTest {

  RecursionBarrier barrier = new RecursionBarrier();

  @Test
  public void shouldStopExecutionWhenEncounteringPreviousVisitedStatement() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    Statement stmt2 = new Statement(createAstNode("myStmt2"));

    barrier.visitStatement(stmt1);
    barrier.visitStatement(stmt2);

    try {
      barrier.visitStatement(stmt1);
    } catch (StopRecursionSignal signal) {
      return;
    }
    throw new AssertionError("A StopPathExplorationSignal was expected.");
  }

  @Test
  public void shouldNotStopExecutionWhenEncounteringPreviousVisitedStatementButWithinAnotherPath() {
    Statement stmt1 = new Statement(createAstNode("myStmt1"));
    Statement stmt2 = new Statement(createAstNode("myStmt2"));

    barrier.visitStatement(stmt1);
    barrier.visitBranch();
    barrier.visitStatement(stmt2);
    barrier.leaveBranch();
    barrier.visitStatement(stmt2);
  }
}
