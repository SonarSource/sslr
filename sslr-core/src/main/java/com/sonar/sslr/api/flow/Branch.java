/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.ArrayList;
import java.util.List;

public class Branch {

  private final List<Branch> children = new ArrayList<Branch>();
  private final Branch parent;
  private final int index;

  public Branch(Branch parent) {
    this.parent = parent;
    parent.children.add(this);
    index = parent.children.size();
  }

  public Branch() {
    parent = null;
    index = 1;
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
    if (branch.parent == null) {
      return false;
    }
    if (branch.parent == this) {
      return true;
    }
    return isParentOf(branch.parent);
  }

  public boolean isParentOrChildOf(Branch branch) {
    return isParentOf(branch) || branch.isParentOf(this);
  }
}
