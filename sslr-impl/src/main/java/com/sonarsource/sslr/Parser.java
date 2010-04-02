/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr;

import java.io.File;

import com.sonarsource.sslr.api.Comments;
import com.sonarsource.sslr.ast.AstNode;

public interface Parser {

  AstNode parse(File sourceFile);

  AstNode parse(String source);

  Comments getComments();

  ParsingState getParsingState();
}
