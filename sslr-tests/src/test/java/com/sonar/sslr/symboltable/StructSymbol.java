/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;

public class StructSymbol extends ScopedSymbol implements Type {

  public StructSymbol(AstNode ast, String name, Scope enclosingScope) {
    super(ast, name, enclosingScope);
  }

  @Override
  public String toString() {
    return "Struct{" + getName() + "}";
  }

}
