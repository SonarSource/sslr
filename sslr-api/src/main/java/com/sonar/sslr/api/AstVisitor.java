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
  List<AstNodeType> getAstNodeTypesToVisit();

  /**
   * Called before starting visiting a computation unit tree. Ideal place to initialize information that is to be collected while processing
   * the tree.
   * 
   * @param ast
   *          the root of the tree
   */
  void visitFile(AstNode ast);

  /**
   * Called just before leaveFile(AstNode ast) method. This method is useful when an AstVisitor needs to share some information with others
   * AstVisitors but can't compute this information without having visited the full AST. In that case, this method beforeLeaveFile(AstNode
   * ast) must be implemented and the others visitor will be able to reused information in the leaveFile(AstNode ast) method.
   * 
   * @param ast
   *          the root of the tree
   */
  void beforeLeaveFile(AstNode ast);

  /**
   * Called once a computation unit tree has been fully visited. Ideal place to report on information collected while processing a tree.
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
