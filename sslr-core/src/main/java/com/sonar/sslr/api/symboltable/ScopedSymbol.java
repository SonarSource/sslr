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
 * Default implementation of named {@link Scope}.
 * Examples: class, method, struct, namespace.
 */
public abstract class ScopedSymbol implements Symbol, Scope {

  private final AstNode astNode;
  private final String name;
  private final Scope enclosingScope;
  private final List<Symbol> members = Lists.newArrayList();
  private final List<Scope> nestedScopes = Lists.newArrayList();
  private final List<Scope> importedScopes = Lists.newArrayList();

  public ScopedSymbol(AstNode astNode, String name, Scope enclosingScope) {
    this.astNode = astNode;
    this.name = name;
    this.enclosingScope = enclosingScope;
    enclosingScope.addNestedScope(this);
  }

  public Scope getEnclosingScope() {
    return enclosingScope;
  }

  public Collection<Scope> getNestedScopes() {
    return nestedScopes;
  }

  public void addNestedScope(Scope nestedScope) {
    nestedScopes.add(nestedScope);
  }

  public Collection<Scope> getImportedScopes() {
    return importedScopes;
  }

  public void importScope(Scope scope) {
    importedScopes.add(scope);
  }

  public void define(Symbol symbol) {
    members.add(symbol);
  }

  public AstNode getAstNode() {
    return astNode;
  }

  public String getName() {
    return name;
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
    return enclosingScope == null ? null : enclosingScope.lookup(name, predicate);
  }

}
