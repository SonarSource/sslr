/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.symboltable;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

import java.util.Collection;
import java.util.List;

public class LocalScope implements Scope {

  private final AstNode ast;
  private final Scope enclosingScope;
  private final List<Symbol> members = Lists.newArrayList();
  private final List<Scope> nestedScopes = Lists.newArrayList();
  private final List<Scope> importedScopes = Lists.newArrayList();

  public LocalScope(AstNode ast) {
    this.ast = ast;
    this.enclosingScope = null;
  }

  public LocalScope(AstNode ast, Scope enclosingScope) {
    this.ast = ast;
    this.enclosingScope = enclosingScope;
    enclosingScope.addNestedScope(this);
  }

  public AstNode getAstNode() {
    return ast;
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

  public Collection<Symbol> getMembers() {
    return members;
  }

  @Override
  public String toString() {
    return "Local scope";
  }

}
