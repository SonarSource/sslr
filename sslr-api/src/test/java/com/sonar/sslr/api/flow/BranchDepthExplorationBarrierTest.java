/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import org.junit.Test;

public class BranchDepthExplorationBarrierTest {

  BranchDepthExplorationBarrier barrier = new BranchDepthExplorationBarrier(3);

  @Test
  public void shouldStopExecutionWhenEncounteringPreviousVisitedStatement() {
    barrier.visitBranch();
    barrier.visitBranch();
    barrier.leaveBranch();
    barrier.visitBranch();

    try {
      barrier.visitBranch();
    } catch (BarrierSignal signal) {
      return;
    }
    throw new AssertionError("A BarrierSignal was expected.");
  }
}
