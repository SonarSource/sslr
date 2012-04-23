/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.symboltable.ScopedSymbol;
import com.sonar.sslr.api.symboltable.SymbolTable;
import com.sonar.sslr.api.symboltable.Type;

public class StructSymbol extends ScopedSymbol implements Type {

  public StructSymbol(SymbolTable symbolTable, String name) {
    super(symbolTable, name);
  }

  @Override
  public String toString() {
    return "Struct{" + getName() + "}";
  }

}
