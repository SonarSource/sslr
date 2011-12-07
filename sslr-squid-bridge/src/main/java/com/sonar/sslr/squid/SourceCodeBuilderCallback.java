/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid;

import org.sonar.squid.api.SourceCode;

import com.sonar.sslr.api.AstNode;

public interface SourceCodeBuilderCallback {

  SourceCode createSourceCode(SourceCode parentSourceCode, AstNode astNode);
}
