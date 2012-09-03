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
package org.sonar.sslr.symboltable;

import com.sonar.sslr.api.AstNode;

import java.util.Collection;

// TODO Godin: maybe split on mutable and immutable?
public interface SemanticModel {

  /**
   * Returns symbol that contains given node.
   */
  Symbol getDeclaredSymbol(AstNode astNode);

  /**
   * Specifies that given node is a declaration of given symbol.
   */
  void declareSymbol(AstNode astNode, Symbol symbol);

  /**
   * Returns enclosing scope that contains given node.
   */
  Scope getEnclosingScope(AstNode astNode);

  /**
   * Specifies that given node is a declaration of given scope.
   */
  void declareScope(AstNode astNode, Scope scope);

  /**
   * Returns references that point to the given symbol.
   */
  Collection<AstNode> getReferences(Symbol symbol);

  /**
   * Specifies that given node references given symbol.
   */
  void declareReference(AstNode astNode, Symbol symbol);

  /**
   * Returns all symbols of a given kind.
   */
  <T extends Symbol> Collection<T> getSymbols(Class<T> kind);

  /**
   * Returns all scopes of a given kind.
   */
  <T extends Scope> Collection<T> getScopes(Class<T> kind);

}
