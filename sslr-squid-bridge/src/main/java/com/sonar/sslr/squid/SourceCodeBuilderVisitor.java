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
public class SourceCodeBuilderVisitor extends SquidAstVisitor<Grammar> {

	private final SquidAstVisitorContext<? extends Grammar> context;
	private final SourceCodeBuilderCallback callback;
	private final AstNodeType[] astNodeTypes;
	
  public SourceCodeBuilderVisitor(SquidAstVisitorContext<? extends Grammar> context, SourceCodeBuilderCallback callback, AstNodeType... astNodeTypes) {
    this.context = context;
    this.callback = callback;
    this.astNodeTypes = astNodeTypes;
  }
  
  @Override
  public void init() {
  	for (AstNodeType astNodeType: astNodeTypes) {
  		subscribeTo(astNodeType);
		}
  }
	
  /**
   * {@inheritDoc}
   */
  @Override
  public void visitNode(AstNode astNode) {
    context.addSourceCode(callback.createSourceCode(context.peekSourceCode(), astNode));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void leaveNode(AstNode astNode) {
  	context.popSourceCode();
  }

}
