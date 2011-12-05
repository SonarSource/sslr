/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import com.sonar.sslr.api.AstNode;

public class Branch {

  private final Branch parent;
  private final int index;
  private int numberOfChildren = 0;
  private Statement stmt;
  private AstNode condition;

  public Branch(Branch parent) {
    this.parent = parent;
    this.parent.numberOfChildren++;
    index = parent.numberOfChildren;
  }

  public Branch() {
    parent = null;
    index = 1;
  }

  public final void setConditionalStatement(Statement stmt) {
    this.stmt = stmt;
  }

  public final void setCondition(AstNode condition) {
    this.condition = condition;
  }

  public final Statement getConditionalStatement() {
    return stmt;
  }

  public final Branch getParent() {
    return parent;
  }

  public final AstNode getCondition() {
    return condition;
  }

  public final int getDepth() {
    if (parent != null) {
      return parent.getDepth() + 1;
    }
    return 1;
  }

  public String toString() {
    return "[BRANCH at level " + getDepth() + " : " + getDepthDetail() + "]";
  }

  public String getDepthDetail() {
    if (parent == null) {
      return "" + index;
    } else {
      return parent.getDepthDetail() + "." + index;
    }
  }

  public boolean isParentOf(Branch branch) {
    Branch parent = branch.parent;
    while (parent != null) {
      if (parent == this) {
        return true;
      }
      parent = parent.parent;
    }
    return false;
  }

  public boolean isParentOrChildOf(Branch branch) {
    return isParentOf(branch) || branch.isParentOf(this);
  }
}
