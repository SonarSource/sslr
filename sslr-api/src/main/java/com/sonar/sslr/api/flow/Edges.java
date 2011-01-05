/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

public abstract class Edges {

  public abstract void processPath(PathExplorer pathExplorer, PathExplorerStack stack);

  public boolean shouldStopCurrentPath() {
    return false;
  }
}
