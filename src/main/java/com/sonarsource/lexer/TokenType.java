/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.lexer;

import com.sonarsource.parser.ast.AstNodeType;

public interface TokenType extends AstNodeType {

  public String getName();

  public String getValue();

  public boolean hasToBeSkippedFromAst();

}
