/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

import com.sonar.sslr.api.AstNode;

/**
 * Default implementation of {@link Symbol}.
 */
public abstract class BaseSymbol implements Symbol {

  private final SymbolTable symbolTable;
  private final String name;

  public BaseSymbol(SymbolTable symbolTable, String name) {
    this.symbolTable = symbolTable;
    this.name = name;
  }

  public AstNode getAstNode() {
    return symbolTable.getAstNode(this);
  }

  public Scope getEnclosingScope() {
    return symbolTable.getEnclosingScope(getAstNode());
  }

  public String getName() {
    return name;
  }

}
