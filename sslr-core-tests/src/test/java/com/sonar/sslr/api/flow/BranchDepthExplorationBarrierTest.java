/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

import org.junit.Test;

public class BranchDepthExplorationBarrierTest {

  @Test
  public void shouldStopFlowExecutionWhenTheDepthOfBranchesIsTooImportant() {
    BranchDepthExplorationBarrier barrier = new BranchDepthExplorationBarrier(3);

    barrier.visitBranch(new Branch()); //Should not throw any flow signal

    try {
      Branch branch = new Branch(new Branch(new Branch()));
      barrier.visitBranch(branch);
    } catch (BarrierSignal signal) {
      return;
    }
    throw new AssertionError("A BarrierSignal was expected.");
  }
}
