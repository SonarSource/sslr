/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.sslr.impl;

import java.io.File;

import com.sonarsource.sslr.api.AstNode;

public interface Parser {

  AstNode parse(File sourceFile);

  AstNode parse(String source);

  ParsingState getParsingState();
}
