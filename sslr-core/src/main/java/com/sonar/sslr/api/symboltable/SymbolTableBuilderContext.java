/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.sonar.sslr.api.AstNode;

import java.util.Collection;
import java.util.IdentityHashMap;

public final class SymbolTableBuilderContext implements SymbolTable {

  private final IdentityHashMap<AstNode, Scope> astToScope = Maps.newIdentityHashMap();
  private final IdentityHashMap<AstNode, Symbol> astToSymbol = Maps.newIdentityHashMap();
  private final Multimap<Symbol, AstNode> references = HashMultimap.create();

  private final IdentityHashMap<SymbolTableElement, AstNode> elementToAstNode = Maps.newIdentityHashMap();

  public void define(AstNode astNode, SymbolTableElement element) {
    elementToAstNode.put(element, astNode);
    if (element instanceof Symbol) {
      Symbol symbol = (Symbol) element;
      astToSymbol.put(astNode, symbol);
      getEnclosingScope(astNode).define(symbol);
    }
    if (element instanceof Scope) {
      astToScope.put(astNode, (Scope) element);
    }
  }

  public AstNode getAstNode(SymbolTableElement element) {
    return elementToAstNode.get(element);
  }

  public Symbol getEnclosingSymbol(AstNode astNode) {
    Symbol result = null;
    while (result == null && astNode != null) {
      result = astToSymbol.get(astNode);
      astNode = astNode.getParent();
    }
    return result;
  }

  public Scope getEnclosingScope(AstNode astNode) {
    Scope result = null;
    while (result == null && astNode != null) {
      result = astToScope.get(astNode);
      astNode = astNode.getParent();
    }
    return result;
  }

  public void addReference(AstNode astNode, Symbol symbol) {
    references.put(symbol, astNode);
  }

  public Collection<AstNode> getReferences(Symbol symbol) {
    return references.get(symbol);
  }

}
