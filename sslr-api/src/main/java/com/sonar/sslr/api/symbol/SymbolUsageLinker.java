/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symbol;

import java.util.HashMap;
import java.util.Map;

import com.sonar.sslr.api.AstNode;

public class SymbolUsageLinker {

  private Map<AstNode, Symbol> usage = new HashMap<AstNode, Symbol>();

  public void linkAstNodeToSymbol(AstNode node, Symbol symbol) {
    usage.put(node, symbol);
  }

  public Symbol getLinkedSymbol(AstNode node) {
    return usage.get(node);
  }
}
