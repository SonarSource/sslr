/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

/**
 * Each AST Node has a type which can be for instance the Rule of a Grammar, a language keyword or a language special token like a WORD.
 * When implementing the AstVisitor interface, its necessary to subscribe to a set of AstNodeType to be notified each time an AstNode with
 * the desired type is visited.
 * 
 * 
 * @see AstVisitor
 * @see Grammar
 * @see AstNode
 */
public interface AstNodeType {

  /**
   * Some AstNode can be pretty useless and makes a global AST less readable. This method allows to automatically remove those AstNode from
   * the AST.
   * 
   * @return true if AstNode with this type must be skipped from the AST.
   */
  public boolean hasToBeSkippedFromAst();
}
