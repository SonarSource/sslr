/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.ScopedSymbol;
import com.sonar.sslr.api.symboltable.SymbolTable;

public class MethodSymbol extends ScopedSymbol {

  public MethodSymbol(SymbolTable symbolTable, Scope enclosingScope, String name) {
    super(symbolTable, name, enclosingScope);
  }

  @Override
  public String toString() {
    return "Method{" + getName() + "}";
  }

}
