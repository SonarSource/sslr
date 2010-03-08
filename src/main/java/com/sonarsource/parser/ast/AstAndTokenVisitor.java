/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.parser.ast;

import com.sonarsource.lexer.Token;

public interface AstAndTokenVisitor extends AstVisitor {

  void visitToken(Token token);
}
