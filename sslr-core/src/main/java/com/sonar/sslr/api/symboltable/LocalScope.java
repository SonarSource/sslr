/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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

  public LocalScope(SymbolTable symbolTable, Scope enclosingScope) {
    this.symbolTable = symbolTable;
    enclosingScope.addNestedScope(this);
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

  public Symbol lookup(String name, Predicate predicate) {
    for (Symbol symbol : getMembers()) {
      if (name.equals(symbol.getName()) && predicate.apply(symbol)) {
        return symbol;
      }
    }
    Scope enclosingScope = getEnclosingScope();
    return enclosingScope == null ? null : enclosingScope.lookup(name, predicate);
  }

  @Override
  public String toString() {
    return "Local";
  }

}
