/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.Symbol;

public class VariableSymbol implements Symbol {

  private final AstNode ast;
  private final String name;

  public VariableSymbol(AstNode ast, String name) {
    this.ast = ast;
    this.name = name;
  }

  public AstNode getAstNode() {
    return ast;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Variable{" + name + "}";
  }

}
