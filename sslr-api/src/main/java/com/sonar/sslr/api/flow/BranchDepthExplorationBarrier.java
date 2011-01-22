/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public class BranchDepthExplorationBarrier extends ExecutionFlowVisitor {

  private final int maximumBranchDepth;
  private int branchDepth = 0;

  public BranchDepthExplorationBarrier(int maximumBranchDepth) {
    this.maximumBranchDepth = maximumBranchDepth;
  }

  @Override
  public void visitBranch() {
    if (branchDepth > maximumBranchDepth) {
      throw new BarrierSignal();
    }
    branchDepth++;
  }

  @Override
  public void leaveBranch() {
    branchDepth--;
  }
}
