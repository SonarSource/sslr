/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

import com.sonar.sslr.api.AstNode;

import java.util.Collection;

public interface SymbolTable {

  /**
   * Returns enclosing symbol for specified AST node.
   */
  Symbol getEnclosingSymbol(AstNode astNode);

  /**
   * Returns enclosing scope for specified AST node.
   */
  Scope getEnclosingScope(AstNode astNode);

  /**
   * Returns AST node for specified element.
   */
  AstNode getAstNode(SymbolTableElement element);

  /**
   * Returns references to specified symbol.
   */
  Collection<AstNode> getReferences(Symbol symbol);

}
