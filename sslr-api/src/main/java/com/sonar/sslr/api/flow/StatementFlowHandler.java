/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public abstract class StatementFlowHandler {

  public abstract void process(PathFinder flowWalker, ControlFlowStack stack);

  public abstract boolean shouldStopCurrentPath();

  public void exploreNewBranch(PathFinder flowWalker, ControlFlowStack stack) {
  }
}
