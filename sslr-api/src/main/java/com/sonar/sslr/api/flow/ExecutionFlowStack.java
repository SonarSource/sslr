/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import java.util.Stack;

public class ExecutionFlowStack {

  private final Stack<FlowHandler> branches = new Stack<FlowHandler>();

  public final boolean isEmpty() {
    return branches.isEmpty();
  }

  public final void add(FlowHandler flowHandler) {
    branches.push(flowHandler);
  }

  public final FlowHandler peek() {
    return branches.peek();
  }

  public FlowHandler pop() {
    return branches.pop();
  }

  public boolean contains(FlowHandler flowHandler) {
    return branches.contains(flowHandler);
  }

}
