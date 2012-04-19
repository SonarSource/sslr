/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.symboltable.Scope;
import com.sonar.sslr.api.symboltable.Type;

public class StructSymbol extends ScopedSymbol implements Type {

  public StructSymbol(AstNode ast, String name, Scope enclosingScope) {
    super(ast, name, enclosingScope);
  }

  @Override
  public String toString() {
    return "Struct{" + getName() + "}";
  }

}
