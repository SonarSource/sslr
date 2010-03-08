/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.parser.ast;

import java.util.List;

public interface AstVisitor {

  List<AstNodeType> getAstNodeTypesToVisit();

  void visitFile(AstNode ast);

  void leaveFile(AstNode ast);

  void visitNode(AstNode ast);

  void leaveNode(AstNode ast);
}
