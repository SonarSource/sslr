/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public class PatternMatcher extends CompositeMatcher {

  public AstNode match(AstNode astNode) {
    return matcher.match(astNode);
  }
}
