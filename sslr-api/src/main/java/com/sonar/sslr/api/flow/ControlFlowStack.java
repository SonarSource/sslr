/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Stack;

public class ControlFlowStack {

  private final Stack<StatementFlowHandler> branches = new Stack<StatementFlowHandler>();

  public final boolean isEmpty() {
    return branches.isEmpty();
  }

  public final void add(StatementFlowHandler flowHandler) {
    branches.push(flowHandler);
  }

  public final StatementFlowHandler peek() {
    return branches.peek();
  }

  public StatementFlowHandler pop() {
    return branches.pop();
  }

  public boolean contains(StatementFlowHandler flowHandler) {
    return branches.contains(flowHandler);
  }

}
