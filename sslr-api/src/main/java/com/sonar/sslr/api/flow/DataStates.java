/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.flow;

abstract public class DataStates {

  public abstract void visitBranch();

  public abstract void leaveBranch();

  public abstract void visitMandatoryBranches();

  public abstract void leaveMandatoryBranches();
}
