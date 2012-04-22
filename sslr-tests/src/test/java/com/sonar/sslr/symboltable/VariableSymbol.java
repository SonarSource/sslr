/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.symboltable.BaseSymbol;
import com.sonar.sslr.api.symboltable.SymbolTable;

public class VariableSymbol extends BaseSymbol {

  public VariableSymbol(SymbolTable symbolTable, String name) {
    super(symbolTable, name);
  }

  @Override
  public String toString() {
    return "Variable{" + getName() + "}";
  }

}
