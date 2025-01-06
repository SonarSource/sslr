/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
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
  boolean hasToBeSkippedFromAst(AstNode node);

}
