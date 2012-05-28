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
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.util.Collection;
import java.util.List;

/**
 * Default implementation of {@link Scope}.
 */
public class LocalScope implements Scope {

  private final SymbolTable symbolTable;
  private final List<Symbol> members = Lists.newArrayList();
  private final List<Scope> nestedScopes = Lists.newArrayList();

  public LocalScope(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
  }

  public AstNode getAstNode() {
    return symbolTable.getAstNode(this);
  }

  public Scope getEnclosingScope() {
    return symbolTable.getEnclosingScope(getAstNode().getParent());
  }

  public Collection<Scope> getNestedScopes() {
    return nestedScopes;
  }

  public void addNestedScope(Scope nestedScope) {
    nestedScopes.add(nestedScope);
  }

  public void define(Symbol symbol) {
    members.add(symbol);
  }

  public Collection<Symbol> getMembers() {
    return members;
  }

  public Symbol resolve(String name, Predicate predicate) {
    for (Symbol symbol : getMembers()) {
      if (name.equals(symbol.getName()) && predicate.apply(symbol)) {
        return symbol;
      }
    }
    Scope enclosingScope = getEnclosingScope();
    return enclosingScope == null ? null : enclosingScope.resolve(name, predicate);
  }

  @Override
  public String toString() {
    return "Local";
  }

}
