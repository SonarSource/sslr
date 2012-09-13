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

import com.google.common.collect.Lists;

import java.util.List;

/**
 * This class provides a skeletal implementation of the {@link Scope}
 * interface to minimize the effort required to implement this interface.
 */
public abstract class AbstractScope implements Scope {

  private final Scope enclosingScope;
  private final List<Symbol> members = Lists.newArrayList();
  private final List<Scope> nestedScopes = Lists.newArrayList();

  public AbstractScope(Scope enclosingScope) {
    this.enclosingScope = enclosingScope;
  }

  public Scope getEnclosingScope() {
    return enclosingScope;
  }

  public List<Scope> getNestedScopes() {
    return nestedScopes;
  }

  public void addNestedScope(Scope nestedScope) {
    nestedScopes.add(nestedScope);
  }

  public void addSymbol(Symbol symbol) {
    members.add(symbol);
  }

  public List<Symbol> getSymbols() {
    return members;
  }

  public <T extends Symbol> List<T> getSymbols(Class<T> kind) {
    List<T> result = Lists.newArrayList();
    for (Symbol symbol : getSymbols()) {
      if (kind.isInstance(symbol)) {
        result.add((T) symbol);
      }
    }
    return result;
  }

  public <T extends Symbol> T getSymbol(Class<T> kind, String key) {
    for (Symbol symbol : getSymbols()) {
      if (kind.isInstance(symbol) && key.equals(symbol.getKey())) {
        return (T) symbol;
      }
    }
    return null;
  }

}
