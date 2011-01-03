/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public class EmptyPathVisitor extends PathVisitor {

  public void start() {
  }

  public void visitStatment(Statement stmt) {
  }

  public void visitBranch(Statement stmt) {
  }

  public void leaveBranch(Statement stmt) {
  }

  public void endPath() {
  }

  public void end() {
  }
}
