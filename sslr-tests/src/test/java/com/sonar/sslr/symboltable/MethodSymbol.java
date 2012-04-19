/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.symboltable.Scope;

import com.sonar.sslr.api.AstNode;

public class MethodSymbol extends ScopedSymbol {

  public MethodSymbol(AstNode ast, Scope enclosingScope, String name) {
    super(ast, name, enclosingScope);
  }

  @Override
  public String toString() {
    return "Method{" + getName() + "}";
  }

}
