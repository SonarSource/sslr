/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import org.sonar.graph.Edge;

public class Path implements Edge<Block> {

  private Block from;
  private Block to;

  public Path(Block from, Block to) {
    this.from = from;
    this.to = to;
  }

  public Block getFrom() {
    return from;
  }

  public Block getTo() {
    return to;
  }

  public int getWeight() {
    throw new UnsupportedOperationException();
  }
}
