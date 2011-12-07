/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symbol;

import com.sonar.sslr.api.AstNode;

public abstract class Symbol {

  private final AstNode node;

  public Symbol(AstNode node) {
    this.node = node;
  }

  public int getStartLine() {
    return node.getTokenLine();
  }

  public int getEndLine() {
    return node.getLastToken().getLine();
  }

  public AstNode getAstNode() {
    return node;
  }
}
