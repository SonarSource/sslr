/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api;

import java.util.List;

public interface AstVisitor {

  /**
   * The AST node types that this check must be registered for.
   * 
   * @return the AST node types this must be registered for.
   */
  List<AstNodeSkippingPolicy> getAstNodeTypesToVisit();

  /**
   * Called before starting visiting a computation unit tree. 
   * Ideal place to initialize information that is to be collected while processing the tree.
   * 
   * @param ast
   *          the root of the tree
   */
  void visitFile(AstNode ast);

  /**
   * Called once a computation unit tree has been fully visited. 
   * Ideal place to report on information collected while processing a tree.
   * 
   * @param ast
   *          the root of the tree
   */
  void leaveFile(AstNode ast);

  /**
   * Called to process an AST node whose type has been registered to be visited.
   * 
   * @param ast
   *          the AST node to process
   */
  void visitNode(AstNode ast);

  /**
   * Called once an AST node has been fully visited.
   * 
   * @param ast
   *          the AST node which has been visited
   */
  void leaveNode(AstNode ast);
}
