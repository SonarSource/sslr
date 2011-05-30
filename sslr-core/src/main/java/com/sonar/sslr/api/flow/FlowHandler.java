/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;


public abstract class FlowHandler<STATEMENT extends Statement> {

  public abstract void processFlow(ExecutionFlowEngine<STATEMENT> flowEngine);
}
