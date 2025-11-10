/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
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

import javax.annotation.Nullable;
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
   *          the root of the tree, or {@code null} if no tree
   */
  void visitFile(@Nullable AstNode ast);

  /**
   * Called once a computation unit tree has been fully visited. Ideal place to report on information collected while processing a tree.
   * 
   * @param ast
   *          the root of the tree, or {@code null} if no tree
   */
  void leaveFile(@Nullable AstNode ast);

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
