/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public abstract class StructuralUnitMatcher {

  public abstract AstNode match(AstNode node);

  public final boolean isMatching(AstNode node) {
    return match(node) != null;
  }
}
