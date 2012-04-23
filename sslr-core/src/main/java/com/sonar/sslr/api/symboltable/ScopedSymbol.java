/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

/**
 * Default implementation of named {@link Scope}.
 * Examples: class, method, struct, namespace.
 */
public abstract class ScopedSymbol extends LocalScope implements Symbol {

  private final String name;

  public ScopedSymbol(SymbolTable symbolTable, String name) {
    super(symbolTable);
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
