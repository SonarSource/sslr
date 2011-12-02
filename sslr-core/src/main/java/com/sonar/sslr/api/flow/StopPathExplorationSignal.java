/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

/**
 * This signal must be used when the end of a path has been reached. For instance when encountering the <code>return;</code> statement with
 * java or the <code>EXIT PROGRAM.</code> statement with COBOL.
 * 
 */
public class StopPathExplorationSignal extends ExecutionFlowSignal {
}
