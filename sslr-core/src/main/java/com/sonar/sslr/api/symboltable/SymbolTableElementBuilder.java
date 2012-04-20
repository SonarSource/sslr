/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.List;

public abstract class SymbolTableElementBuilder {

  private final List<AstNodeType> nodeTypes;

  public SymbolTableElementBuilder(AstNodeType... nodeTypes) {
    this.nodeTypes = ImmutableList.of(nodeTypes);
  }

  public List<AstNodeType> getNodeTypes() {
    return nodeTypes;
  }

  public abstract void visitNode(AstNode astNode, SymbolTableBuilderContext symbolTable);

}
