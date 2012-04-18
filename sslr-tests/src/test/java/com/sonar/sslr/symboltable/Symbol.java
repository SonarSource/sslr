/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;

/**
 * Entity of program, which can be referenced.
 */
public interface Symbol {

  /**
   * Returns associated AST node.
   */
  AstNode getAstNode();

  /**
   * Returns name of this symbol.
   */
  String getName();

  // TODO Godin: do we want to be able to determine scope, where this symbol was defined?
  // In fact this will be straightforward, if AST node will know enclosing scope.

}
