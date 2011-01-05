/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Observable;

public abstract class PathVisitor extends Observable {
  
  public void start() {
  }

  public void visitStatement(Statement stmt) {
  }

  public void visitBranch() {
  }

  public void leaveBranch() {
  }

  public void endPath() {
  }

  public void end() {
  }
  
  protected final void stopVisitingPath(){
    setChanged();
    notifyObservers();
  }
}
