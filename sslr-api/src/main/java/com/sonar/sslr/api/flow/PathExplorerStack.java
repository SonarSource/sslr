/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Stack;

public class PathExplorerStack {

  private final Stack<Edges> branches = new Stack<Edges>();

  public final boolean isEmpty() {
    return branches.isEmpty();
  }

  public final void add(Edges flowHandler) {
    branches.push(flowHandler);
  }

  public final Edges peek() {
    return branches.peek();
  }

  public Edges pop() {
    return branches.pop();
  }

  public boolean contains(Edges flowHandler) {
    return branches.contains(flowHandler);
  }

}
