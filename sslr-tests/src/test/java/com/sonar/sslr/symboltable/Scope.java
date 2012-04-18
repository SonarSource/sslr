/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;

import java.util.Collection;

/**
 * Region of code with a well-defined boundaries that groups symbol definitions.
 */
public interface Scope {

  /**
   * Returns associated AST node.
   */
  AstNode getAstNode();

  /**
   * Returns enclosing scope.
   */
  Scope getEnclosingScope();

  /**
   * Returns all nested scopes.
   */
  Collection<Scope> getNestedScopes();

  /**
   * TODO Godin: in fact tree of scopes should be immutable after construction.
   * However question: how we can achieve this, if this interface might be implemented by clients?
   */
  void addNestedScope(Scope nestedScope);

  /**
   * TODO comment me
   */
  Collection<Scope> getImportedScopes();

  /**
   * TODO Godin: in fact tree of scopes should be immutable after construction.
   * However question: how we can achieve this, if this interface might be implemented by clients?
   */
  void importScope(Scope scope);

  /**
   * Returns all symbols defined in this scope.
   */
  Collection<Symbol> getMembers();

  /**
   * TODO Godin: in fact tree of scopes should be immutable after construction.
   * However question: how we can achieve this, if this interface might be implemented by clients?
   */
  void define(Symbol symbol);

}
