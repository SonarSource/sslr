/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.AstNode;

public class DirectChildNodeMatcher extends ChildNodeMatcher {

  @Override
  public AstNode matchChildren(AstNode node) {
    for (AstNode child : node.getChildren()) {
      if (child.getName().equals(ruleName)) {
        return child;
      }
    }
    return null;
  }
}
