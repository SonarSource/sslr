/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

/**
 * Specific Ast node types that can tell whether they should be skipped from being attached to the AST or not.
 * 
 * 
 * @see AstVisitor
 * @see Grammar
 * @see AstNode
 */
public interface AstNodeSkippingPolicy extends AstNodeType {

  /**
   * Some AstNode can be pretty useless and makes a global AST less readable. This method allows to automatically remove those AstNode from
   * the AST.
   * 
   * @param node
   *          the node that should or not be removed from the AST
   * @return true if AstNode with this type must be skipped from the AST.
   */
  public boolean hasToBeSkippedFromAst(AstNode node);
}
