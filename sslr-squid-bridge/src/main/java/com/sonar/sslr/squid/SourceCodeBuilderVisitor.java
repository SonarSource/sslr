/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;

/**
 * Visitor that create resources.
 */
public class SourceCodeBuilderVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final SourceCodeBuilderCallback callback;
  private final AstNodeType[] astNodeTypes;

  public SourceCodeBuilderVisitor(SourceCodeBuilderCallback callback, AstNodeType... astNodeTypes) {
    this.callback = callback;
    this.astNodeTypes = astNodeTypes;
  }

  @Override
  public void init() {
    for (AstNodeType astNodeType : astNodeTypes) {
      subscribeTo(astNodeType);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitNode(AstNode astNode) {
    getContext().addSourceCode(callback.createSourceCode(getContext().peekSourceCode(), astNode));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void leaveNode(AstNode astNode) {
    getContext().popSourceCode();
  }

}
