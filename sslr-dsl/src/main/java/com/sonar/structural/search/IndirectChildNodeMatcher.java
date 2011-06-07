/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.AstNode;

public class IndirectChildNodeMatcher extends ChildNodeMatcher {

  @Override
  public AstNode matchChildren(AstNode node) {
    if (node.hasChildren()) {
      for (AstNode child : node.getChildren()) {
        if (child.getName().equals(ruleName)) {
          return child;
        }
      }
      for (AstNode child : node.getChildren()) {
        return matchChildren(child);
      }
    }
    return null;
  }
}
