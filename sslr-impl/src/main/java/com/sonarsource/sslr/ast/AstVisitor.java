/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr.ast;

import java.util.List;

import com.sonarsource.sslr.api.AstNodeType;

public interface AstVisitor {

  List<AstNodeType> getAstNodeTypesToVisit();

  void visitFile(AstNode ast);

  void leaveFile(AstNode ast);

  void visitNode(AstNode ast);

  void leaveNode(AstNode ast);
}
