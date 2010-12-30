/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api.flow;

import org.sonar.graph.Edge;

import com.sonar.sslr.api.AstNode;

public class Path implements Edge<Block> {

  private Block from;
  private Block to;
  private ConditionalPathType type;

  public Path(Block from, Block to) {
    this.from = from;
    this.to = to;
  }

  public Path(Block from, Block to, AstNode condition, boolean expectedConditionValue) {
    this.from = from;
    this.to = to;
    this.type = new ConditionalPathType(condition, expectedConditionValue);
  }

  public Block getFrom() {
    return from;
  }

  public Block getTo() {
    return to;
  }

  public boolean isConditionalPath() {
    return type != null && type instanceof ConditionalPathType;
  }

  public ConditionalPathType getConditionalPathType() {
    return type;
  }

  public int getWeight() {
    throw new UnsupportedOperationException();
  }
}
