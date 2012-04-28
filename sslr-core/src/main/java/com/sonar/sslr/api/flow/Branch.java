/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
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

  @Override
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
    Branch branchParent = branch.parent;
    while (branchParent != null) {
      if (branchParent == this) {
        return true;
      }
      branchParent = branchParent.parent;
    }
    return false;
  }

  public boolean isParentOrChildOf(Branch branch) {
    return isParentOf(branch) || branch.isParentOf(this);
  }
}
