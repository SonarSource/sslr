/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

import com.sonar.sslr.api.AstNode;

/**
 * Entity of program, which can be referenced.
 */
public interface Symbol extends SymbolTableElement {

  /**
   * Returns associated AST node.
   */
  AstNode getAstNode();

  /**
   * Returns enclosing scope.
   */
  Scope getEnclosingScope();

  /**
   * Returns name of this symbol.
   */
  String getName();

}
