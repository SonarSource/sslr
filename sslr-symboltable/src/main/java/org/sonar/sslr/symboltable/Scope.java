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

import java.util.List;

/**
 * Region of code with a well-defined boundaries that groups symbol definitions.
 *
 * @see AbstractScope
 * @see AbstractScopedSymbol
 * @since 1.15
 */
public interface Scope {

  /**
   * Returns the scope enclosing this one or null if none.
   */
  Scope getEnclosingScope();

  void addNestedScope(Scope nestedScope);

  /**
   * Returns all nested scopes.
   */
  List<Scope> getNestedScopes();

  void addSymbol(Symbol symbol);

  /**
   * Returns all symbols defined in this scope.
   */
  List<Symbol> getSymbols();

  /**
   * Returns all symbols from this scope of a given kind.
   */
  <T extends Symbol> List<T> getSymbols(Class<T> kind);

  /**
   * Returns symbol from this scope of a given kind and with given key.
   */
  <T extends Symbol> T getSymbol(Class<T> kind, String key);

}
