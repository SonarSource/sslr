/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.sslr.api.symboltable;

import com.google.common.base.Predicate;
import com.sonar.sslr.api.AstNode;

import java.util.Collection;

/**
 * Region of code with a well-defined boundaries that groups symbol definitions.
 */
public interface Scope extends SymbolTableElement {

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
   * Returns all symbols defined in this scope.
   */
  Collection<Symbol> getMembers();

  /**
   * TODO Godin: in fact tree of scopes should be immutable after construction.
   * However question: how we can achieve this, if this interface might be implemented by clients?
   */
  void define(Symbol symbol);

  Symbol lookup(String name, Predicate predicate);

}
