/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

/**
 * Must be used to stop visiting the execution flow. When this execution flow signal is thrown, the method stop() is executed on all
 * {@link ExecutionFlowVisitor}.
 * 
 * @see {@link ExecutionFlowVisitor}
 */
public class StopFlowExplorationSignal extends ExecutionFlowSignal {
}
