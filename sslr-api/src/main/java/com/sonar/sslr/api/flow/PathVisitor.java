/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public abstract class PathVisitor {
  
  public abstract void start();

  public abstract void visitStatment(Statement stmt);
  
  public abstract void visitBranch(Statement stmt);
  
  public abstract void leaveBranch();
  
  public abstract void endPath();
  
  public abstract void end();
}
