/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser;

import java.io.File;

import com.sonarsource.parser.ast.AstNode;

public interface Parser {

  AstNode parse(File sourceFile);

  AstNode parse(String source);

  ParsingState getParsingState();
}
