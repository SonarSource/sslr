/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

/**
 * A Barrier signal is used to silently stop exploring a path. This signal can be used for instance to limit the depth of the exploration or
 * to prevent some recursions.
 */
public class BarrierSignal extends ExecutionFlowSignal {
}
